# Implementation Plan: Replicate Issuing Raw Materials to Production Logic in Cost Center Issuing

Replicate the logic and UI structure from `IssuingRawMaterialsToProductionScreen` and `IssueRawMaterialsToProductionDetailsScreen` to `IssuingCostCenterOrdersScreen` and `IssueCostCenterDetailsScreen`, using the specific UseCases for Cost Center issuing and setting the accent color to orange.

## User Review Required

> [!IMPORTANT]
> The `DirectIssueOnCostCenterLineDto` does not include `receivingPlantCode` or `receivingWarehouseCode`, as it's a direct issue on a cost center rather than a transfer between warehouses. The warehouse selection UI will be omitted from the Cost Center details screen.

## Proposed Changes

### Issuing Cost Center - Orders List

#### [MODIFY] [IssueOrdersListViewModel.kt](file:///H:/GBSAndroidProjects/GBSWarehouseJetPackCompose/app/src/main/java/net/gbs/gbswarehouse/ui/issue/issuing_cost_center/orders_list/IssueOrdersListViewModel.kt)
- Inject `GetDirectIssueOnCostCenterReservationsUseCase`.
- Update `orders` state to use `SparePartsReturnReservationOrder`.
- Implement `loadOrders()` using the UseCase.
- Update `filteredOrders` logic.
- Add `isLoading` and `errorMessage` states.

#### [MODIFY] [IssueOrdersListScreens.kt](file:///H:/GBSAndroidProjects/GBSWarehouseJetPackCompose/app/src/main/java/net/gbs/gbswarehouse/ui/issue/issuing_cost_center/orders_list/IssueOrdersListScreens.kt)
- Update `IssuingCostCenterOrdersScreen` to handle loading and error states.
- Use `IssueOrderCard` (which will be shared or copied) to display order details.
- Ensure the accent color is orange.
- Link to the correct details screen route.

### Issuing Cost Center - Details Screen

#### [NEW] [IssueDetailsState.kt](file:///H:/GBSAndroidProjects/GBSWarehouseJetPackCompose/app/src/main/java/net/gbs/gbswarehouse/ui/issue/issuing_cost_center/details_screen/IssueDetailsState.kt)
- Create a state class for the Cost Center details screen, using `SparePartsReturnReservationLine` and `DirectIssueOnCostCenterLineDto`.

#### [NEW] [IssueDetailsEvent.kt](file:///H:/GBSAndroidProjects/GBSWarehouseJetPackCompose/app/src/main/java/net/gbs/gbswarehouse/ui/issue/issuing_cost_center/details_screen/IssueDetailsEvent.kt)
- Create an event class for the Cost Center details screen.

#### [MODIFY] [IssueDetailsViewModel.kt](file:///H:/GBSAndroidProjects/GBSWarehouseJetPackCompose/app/src/main/java/net/gbs/gbswarehouse/ui/issue/issuing_cost_center/details_screen/IssueDetailsViewModel.kt)
- Inject the following UseCases:
    - `GetDirectIssueOnCostCenterReservationLinesUseCase`
    - `DirectIssueOnCostCenterUseCase`
    - `GetStorageBinLocationIssuingUseCase`
    - `GetMaterialStockUseCase`
- Implement the core logic: `loadOrderDetails`, `scanBin`, `selectReservationLine`, `loadStock`, `addLine`, and `issueAll`.
- Note: `DirectIssueOnCostCenterLineDto` only needs the source warehouse code (from the reservation line) and the scanned bin.

#### [MODIFY] [IssueDetailsScreen.kt](file:///H:/GBSAndroidProjects/GBSWarehouseJetPackCompose/app/src/main/java/net/gbs/gbswarehouse/ui/issue/issuing_cost_center/details_screen/IssueDetailsScreen.kt)
- Replicate the UI layout from `IssueRawMaterialsToProductionDetailsScreen`.
- Set the accent color to ORANGE.
- Update UI components to use the new `IssueDetailsState` and `IssueDetailsEvent`.
- Remove the warehouse selection spinner since it's not applicable for Cost Center issuing.

---

## Verification Plan

### Automated Tests
- N/A (Manual verification on device is preferred for UI/Scanner integration).

### Manual Verification
1. Deploy the app to a device with a Honeywell scanner.
2. Navigate to "صرف مباشر علي مركز تكلفة".
3. Verify the orders list loads correctly from the API.
4. Search for an order and select it.
5. In the details screen:
    - Scan/Enter a Bin Code.
    - Scan/Select a Material.
    - Select a Batch (if managed).
    - Enter quantity and add to list.
    - Confirm issuing and verify success message.
6. Verify all UI elements use the orange accent color.
