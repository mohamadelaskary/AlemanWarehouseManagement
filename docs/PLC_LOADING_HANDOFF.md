# PLC Loading — Agent Handoff

## 1. CURRENT STATUS
- PHASE 1–7 complete and compiling. PHASE 8 (device verification) partially done.
- Build is green: `.\gradlew.bat :app:assembleDebug --console=plain` → **BUILD SUCCESSFUL**.
- APK installed and exercised on device `192.168.1.5:5556`.
- **Orders are REAL**, from `api/Conveyors/GetLoadingOrders`. Only the conveyor/operation
  state is still simulated. There is **no single-order endpoint**.
- Remaining blocker for full PHASE 8: reaching Orders requires a **real backend login** (see §8).

## 2. ARCHITECTURE
UI → ViewModel → UseCase → `LoadingRepository` → {`ApiService` (real orders), `PlcDataSource`
(simulated PLC)}. No UI touches the fake engine directly. Swapping in a real PLC later means
reimplementing `PlcDataSource` only.

### PHASE 1 — inspection
No pre-existing Service / Notification / Audio / MediaPlayer code; manifest had only
`MainActivity` + `INTERNET`/`ACCESS_NETWORK_STATE`; no `res/raw`; no new dependencies added.

### PHASE 2 — orders API (real) + PLC simulation (fake)
- `data/remote/dto/response/LoadingOrderDto.kt` — `LoadingOrderDto` + `LoadingOrderLineDto`,
  every field from the response. Gson converter, camelCase names match, no annotations needed.
  Kept in a dedicated file so it can't collide with ongoing edits to `AllDtosClasses.kt`.
- `data/remote/mapper/LoadingMapper.kt` — `toDomain()`. Mapping:
  `goodsReceiptId`→id, `receiptNo`→orderNumber, `carNo`→vehicleNumber,
  `suggestedBags`→requiredQuantity, `loadedBags`→loadedQuantity,
  `materialDescription`→item name. `remainingSuggestedBags` is mapped but is always
  `suggestedBags - loadedBags`, i.e. identical to the computed `remainingQuantity`, so it is
  not displayed.
- `data/remote/api/ApiService.kt` — `@GET("api/Conveyors/GetLoadingOrders")`, no body,
  returns `MobileResponse<List<LoadingOrderDto>>`.
- `domain/model/LoadingModels.kt` — `LoadingOrder`/`OrderItem` extended with status,
  shippingPointCode, workerGroupCode, createdAt / salesOrder(+Item), materialCode, itemText,
  quantity, uom, plantCode, bagLengthMm, bagWeightKg, remainingSuggestedBags.
- `data/fake/PlcDataSourceImpl.kt` — `@Singleton` simulated engine owning all conveyor state.
  Seeds are **real records from the API response** so simulated operations point at rows that
  exist in the backend list:
  - Conveyor 2 RUNNING → `goodsReceiptId 140` / `GR-20260929-002603782`, line 144, 8/20 bags
  - Conveyor 3 PAUSED → `goodsReceiptId 130` / `GR-20260928-193238469`, line 133, 0/60 bags
  - Conveyor 1 IDLE, Conveyor 4 ERROR
  `BAG_INTERVAL_MS = 1_000L`, `COMPLETION_DELAY_MS = 10_000L`, ids `OP-00001`.
  `manualStop` on RUNNING/PAUSED ⇒ operation ERROR + `MANUAL_STOP_REASON`.
- `data/fake/LoadingRepositoryImpl.kt` — combines both sources:
  - `getOrders()` hits the endpoint via `safeApiCall { }.map { }` and caches the mapped list.
  - `getOrderDetails(orderId)` **never calls a detail endpoint**; it serves the clicked order
    from that cache, and only falls back to one list fetch if the cache is cold (process restart).
  - `getCachedOrders()` returns the cached list with live PLC progress projected, no network.
  - `withLiveProgress()` overlays simulated operation progress onto real item quantities.
- **Bag counts are absolute.** `startLoading` creates the operation with
  `requiredQuantity = suggestedBags` and `loadedQuantity = loadedBags`, so an operation
  continues from the bags the backend already recorded and completes at `suggestedBags`.
  (Previously it reset `loaded` to 0 and used `remainingQuantity`, which would have wiped the
  backend's 8 loaded bags.)
- `domain/usecase/loading/LoadingUseCases.kt` — 16 use cases + aggregator
  (`getCachedLoadingOrders` added for the cache-only read).
- `di/LoadingModule.kt` / `di/RepositoryModule.kt` — provide `LoadingUseCases`, bind both singletons.
- Strings mirrored in `values/strings.xml` + `values-ar/strings.xml`.

### PHASE 3 — UI
- `ui/plc/PlcUiHelpers.kt` — `PLC_ACCENT`, `ConveyorStatus.label()`, `ConveyorStatus.color()`,
  plus `String?.orDash()` and `Double.orTrimmed()` for the optional backend fields.
- `ui/plc/orders/` — search list; shows shipping point + status; VM re-reads **from cache** on
  every operation change so quantities stay live without spamming the backend per bag.
- `ui/plc/order_details/` — status pill, shipping point, worker group, created at; item cards show
  material code / UOM / plant / sales order(+item) / bag weight / bag length; the conveyor picker
  also shows order quantity + bag weight. Item cards stay inline (accepted, see §6).
- `ui/plc/conveyors/` — live conveyor cards with manual start/stop.
- Routes in `Screens.kt`: `LoadingOrders`, `LoadingOrderDetails/{orderId}`, `LoadingConveyors`,
  `LoadingOperation/{operationId}`. `HomeScreen.kt` has a `"plc"` card → `LoadingOrders`.

### PHASE 4/5 — Loading screen + audio
- `ui/plc/loading/LoadingOperationScreen.kt` + VM — progress bar, status pill, operation info,
  quantity cells, `LoadingControls` (central action-availability rules derived from operation state
  only), completed/failed section, `ReportFailureDialog`.
- `audio/LoadingSoundPlayer.kt` — `@Singleton`, `ToneGenerator` only (no assets, no deps).
  Distinct tones: bag `TONE_PROP_BEEP`, success `TONE_CDMA_ALERT_CALL_GUARD`,
  failure `TONE_CDMA_ABBR_REORDER`. Driven by PLC **events**, never recomposition.

### PHASE 6/7 — service + notifications + navigation
- `service/LoadingService.kt` — `dataSync` FGS; collects `events` + `operations` for one
  `operationId`; plays tones; posts active/result notifications; `stopTracking()` keeps the
  result notification then stops. `EXTRA_OPERATION_ID` is the only payload.
- `service/LoadingNotificationHelper.kt` — 3 channels. ACTIVE silent/low. SUCCESS and FAILURE are
  also **silent** on purpose: `LoadingSoundPlayer` owns audible feedback, so channel sounds would
  double-play and both result channels would sound identical.
- `core/loading/LoadingNavigation.kt` — Activity→Compose one-shot handoff.
- `MainActivity.kt` — handles the intent extra, requests `POST_NOTIFICATIONS` on API 33+,
  and **removes the extra after reading** so `recreate()` doesn't replay the navigation.
- Manifest: `FOREGROUND_SERVICE`, `FOREGROUND_SERVICE_DATA_SYNC`, `POST_NOTIFICATIONS`.

## 3. BUGS FOUND AND FIXED DURING PHASE 8
1. **Notification tap did nothing.** `navController.navigate()` ran in a `LaunchedEffect`
   registered before `NavHost`, i.e. before the graph was installed. Fixed by awaiting
   `navController.currentBackStackEntryFlow.first()` first (`WarehouseApp.kt`).
2. **Notification navigation replayed on config change.** `MainActivity` never cleared the extra.
   Now removed after handling.
3. **Orders showed stale quantities** after bags were counted, and allowed re-loading a finished
   item. Fixed via `LoadingRepositoryImpl.withLiveProgress()` + VM re-read on operation changes.
4. **Conveyors screen unreachable** from the Orders screen (only from a finished operation).
   Added a `FullBtn` entry to `LoadingOrdersScreen`.
5. **Duplicate/indistinct result audio.** Result channels used the default notification sound
   while `LoadingSoundPlayer` played tones. Result channels are now silent.

## 4. VERIFIED ON DEVICE (192.168.1.5:5556, Android 8.1)
Driven with `uiautomator dump` + `input tap` (UI hierarchy text, not screenshots).

| Scenario | Result |
|---|---|
| App launch / language toggle AR→EN | OK, no crash |
| Notification intent → Loading screen | OK (after fix #1) |
| Progress ticks 1 bag/sec | OK (37→42→77 over ~35s) |
| Pause halts counting | OK (held at 96 across 8s) |
| Resume restarts ticker | OK (100/100 reached) |
| Completion after 10s at full qty | OK ("Loading completed successfully") |
| Add One Pack (while paused, deterministic) | OK (25 → 26) |
| Report Failure | OK → ERROR, reason shown, Manual Start offered |
| Conveyors screen, all 4 | OK — C1 Idle, C2 Completed 100/100, C3 Paused OP-00002, C4 Error |
| **Re-verify after API switch (real seeds)** | OK — OP-00001 on Conveyor 2 resolved `carNo 30000363` and the real `materialDescription`; count ran **8 → 13 → 20 / 20** (proving the absolute-bag-count fix) and completed |

No `FATAL EXCEPTION` / `AndroidRuntime` error in logcat at any point.

## 5. NOT YET VERIFIED
- **The real `GetLoadingOrders` response has never been fetched on-device** — Orders sits behind
  login, so the DTO mapping, the cache, and the "show all statuses" list are unproven at runtime.
  This is the single highest-value remaining check.
- Orders screen live progress, Orders → Conveyors button, order details → conveyor picker →
  **starting a new operation against a real order**.
- `LoadingService` foreground notification, bag beep, success/failure notifications, manual stop.
  The service is correctly **not exported**, so `adb am start-foreground-service` is refused; it must
  be started in-app once an operation starts.

## 6. KNOWN LIMITATIONS / DECISIONS
- **State is process-local — accepted.** `PlcDataSourceImpl` is an in-memory `@Singleton`, so PLC state
  is lost on process death. Observed on device: the process was killed while the screen was locked and
  state reset to seed. `LoadingService` returns `START_STICKY` but stops itself when the restart intent
  has no `EXTRA_OPERATION_ID`, so cold restore of an active operation is intentionally unsupported.
  Decision: keep it in-memory for the fake PLC; revisit only if a real backend makes it matter.
- **Inline item cards are intentional — accepted.** There is no separate item-selection bottom sheet;
  item cards sit inline on Order Details and tapping one opens the conveyor picker.
- `SessionManager` defaults to `Authenticated`, so the notification-navigation guard is effectively
  a no-op; a cold-start notification tap can navigate while the user is at the login screen.
- Manual Start on an `ERROR` operation moves the conveyor to `MANUAL` but leaves the finished
  operation attached, so the Conveyors card then shows no manual-stop control (only via the
  Loading screen). Worth a UX pass.
- `LoadingOperationViewModel.bind()` starts a new collector each call without cancelling the previous.

## 7. BUILD / RUN NOTES
- Kotlin incremental compiler can report a **varying** error set between runs; assume the list may
  differ on a clean build.
- **`:app:clean` intermittently dies with `e: Daemon compilation failed`**, followed by a flood of
  bogus `cannot find symbol` errors in `app/build/generated/ksp/...` files (`*_Factory.java`,
  `*_HiltModules.java`). This is the Kotlin compile daemon crashing, **not** a source error — the
  generated sources are fine. Just re-run `:app:assembleDebug` and it succeeds. Don't "fix" the
  generated files.
- Gradle/Kotlin daemons persist after a build; a silent run hides `BUILD SUCCESSFUL`. Don't use `-q`.
- Device: `adb -s 192.168.1.5:5556 shell svc power stayon true` — the screen otherwise sleeps mid-test.
- On this device the **lock screen steals focus**: `am start` reports "task brought to the front" but
  `mCurrentFocus` stays on `StatusBar`. Unlock first with
  `input keyevent KEYCODE_WAKEUP` + `input swipe 360 1000 360 300 200`, then start the activity.
- APK: `app\build\outputs\apk\debug\app-debug.apk`.

## 8. OPEN QUESTION FOR THE USER
Login hits a real backend (`SettingsPrefs` currently `http://100.99.50.72:433`). A still-valid
`access_token` and `user_id` are in `shared_prefs`, so this device has logged in before, but the
stored token is not used to satisfy the app's own login screen. Test credentials were requested so
PHASE 8 service/notification testing can continue; the device must be reachable on that backend
(it is behind Tailscale on this network).

## 9. DO NOT REPEAT
- Don't add libraries or bump Gradle.
- Don't add `res/raw` audio — tones are generated.
- Don't re-add the theme toggle to `TopBar` (already there).
- Don't run builds with `-q`.
- Preserve unrelated working-tree changes; `domain/model/Message.kt` is the user's own work.