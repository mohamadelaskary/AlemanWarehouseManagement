# Implementation Plan - Put Away Screens

Create a unified Put Away feature that works for all receiving types. This includes an order selection screen and a detailed put away execution screen with bin and material scanning.

## User Review Required

> [!IMPORTANT]
> The new screens will be generic and handle different `receiptType` values passed via navigation.
> The accent color will be set to `YELLOW` (0xFFFFB800) as requested.
> I will add a "Clear Bin" button to allow scanning a new bin while keeping the material flow efficient.

## Proposed Changes

### [Data Layer]

#### [MODIFY] [AllDtosClasses.kt](file:///H:/GBSAndroidProjects/GBSWarehouseJetPackCompose/app/src/main/java/net/gbs/gbswarehouse/data/remote/dto/response/AllDtosClasses.kt)
- Add `PutAwayOrderDto`, `PutAwayOrderLineDto`, `StorageBinLocationDto`.
- Add `ConfirmPutAwayRequestDto`.

#### [MODIFY] [ApiService.kt](file:///H:/GBSAndroidProjects/GBSWarehouseJetPackCompose/app/src/main/java/net/gbs/gbswarehouse/data/remote/api/ApiService.kt)
- Add the following endpoints:
    - `GET api/ReceivingHH/GetPutAwayOrders`
    - `GET api/ReceivingHH/GetPutAwayOrderLines`
    - `GET api/ReceivingHH/GetStorageBinLocation`
    - `POST api/ReceivingHH/ConfirmPutAway`

#### [MODIFY] [ReceivingRepository.kt](file:///H:/GBSAndroidProjects/GBSWarehouseJetPackCompose/app/src/main/java/net/gbs/gbswarehouse/domain/repository/ReceivingRepository.kt)
- Add domain methods for the new Put Away operations.

#### [MODIFY] [ReceivingRepositoryImpl.kt](file:///H:/GBSAndroidProjects/GBSWarehouseJetPackCompose/app/src/main/java/net/gbs/gbswarehouse/data/remote/repository/ReceivingRepositoryImpl.kt)
- Implement the new repository methods.

### [UI Layer]

#### [MODIFY] [Screens.kt](file:///H:/GBSAndroidProjects/GBSWarehouseJetPackCompose/app/src/main/java/net/gbs/gbswarehouse/Screens.kt)
- Add `PutAwayOrders` and `PutAwayDetails` routes with `receiptType` and `orderNumber` parameters.

#### [NEW] [PutAwayOrdersViewModel.kt](file:///H:/GBSAndroidProjects/GBSWarehouseJetPackCompose/app/src/main/java/net/gbs/gbswarehouse/ui/receiving/put_away/orders_list/PutAwayOrdersViewModel.kt)
- ViewModel to fetch and manage the list of put away orders.

#### [NEW] [PutAwayOrdersScreen.kt](file:///H:/GBSAndroidProjects/GBSWarehouseJetPackCompose/app/src/main/java/net/gbs/gbswarehouse/ui/receiving/put_away/orders_list/PutAwayOrdersScreen.kt)
- UI for displaying the list of orders.

#### [NEW] [PutAwayDetailsViewModel.kt](file:///H:/GBSAndroidProjects/GBSWarehouseJetPackCompose/app/src/main/java/net/gbs/gbswarehouse/ui/receiving/put_away/details_screen/PutAwayDetailsViewModel.kt)
- ViewModel to handle bin validation, material scanning, and confirmation logic.

#### [NEW] [PutAwayDetailsScreen.kt](file:///H:/GBSAndroidProjects/GBSWarehouseJetPackCompose/app/src/main/java/net/gbs/gbswarehouse/ui/receiving/put_away/details_screen/PutAwayDetailsScreen.kt)
- UI for performing the put away, including BottomSheet for material selection.

#### [MODIFY] [WarehouseApp.kt](file:///H:/GBSAndroidProjects/GBSWarehouseJetPackCompose/app/src/main/java/net/gbs/gbswarehouse/WarehouseApp.kt)
- Register the new routes in the `NavHost`.

## Verification Plan

### Automated Tests
- Build the project to ensure no compilation errors.
- (Optional) Add unit tests for `PutAwayDetailsViewModel` to verify quantity and bin validation logic.

### Manual Verification
- Deploy to device/emulator.
- Navigate to Put Away for a specific type (e.g., Spare Parts).
- Verify orders are loaded.
- Select an order and verify details screen shows header data.
- Scan/Enter a valid Bin and verify API call.
- Scan/Enter a Material or select from BottomSheet.
- Enter quantity and confirm.
- Verify success clears material data but keeps bin data.
- Verify "Clear Bin" works.
