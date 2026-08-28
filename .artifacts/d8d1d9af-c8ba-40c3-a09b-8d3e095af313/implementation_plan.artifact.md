# Implement Receiving Raw Materials from Vendor Screens

Implement the list and details screens for receiving raw materials from a vendor, integrating with the specified UseCases and adding a customized material card.

## Proposed Changes

### UI Components

#### [MODIFY] [DetailsScreens.kt](file:///H:/GBSAndroidProjects/GBSWarehouseJetPackCompose/app/src/main/java/net/gbs/gbswarehouse/ui/receiving/receiving_raw_materials_from_vendor/details_screen/DetailsScreens.kt)
- Add `SelectedTransitMaterialCard` composable with the following fields:
    - `WarehousesSpinner` (searchable)
    - `CarNumberSpinner` (searchable)
    - `UnloadingPoint` (text field)
    - `WorkersGroupSpinner` (searchable)
    - `NaolonDescriptionSpinner` (searchable)
    - `CarTypeSpinner` (searchable)
    - `ContNo` (text field)
    - `LoadingVendorSpinner` (searchable)
    - `NaolonDisplay` (read-only field showing value from API)
    - `PackageTypeSpinner` (searchable)
    - `AddData` button to add the item to the list.
- Update `ReceivingRawMaterialsFromVendorDetailsScreen` to use this card and show the list of added items.

### ViewModels

#### [MODIFY] [OrdersListViewModel.kt](file:///H:/GBSAndroidProjects/GBSWarehouseJetPackCompose/app/src/main/java/net/gbs/gbswarehouse/ui/receiving/receiving_raw_materials_from_vendor/orders_list/OrdersListViewModel.kt)
- Refactor to use `GetAllRawMaterialsPurchaseOrdersUseCase`.
- Use Hilt for dependency injection.
- Implement fetching and filtering orders from the API.

#### [MODIFY] [DetailsViewModel.kt](file:///H:/GBSAndroidProjects/GBSWarehouseJetPackCompose/app/src/main/java/net/gbs/gbswarehouse/ui/receiving/receiving_raw_materials_from_vendor/details_screen/DetailsViewModel.kt)
- Refactor to use:
    - `GetRawMaterialsPurchaseOrderDataUseCase`
    - `ReceiveRawMaterialsUseCase`
    - `GetPlantWarehousesUseCase`
    - `GetAllCarNumbersUseCase`
    - `GetAllWorkerGroupsUseCase`
    - `GetAllNaolonDescriptionsUseCase`
    - `GetAllCarTypesUseCase`
    - `GetAllLoadingVendorsUseCase`
    - `GetAllPackageTypesUseCase`
    - `GetNaolonPerTonValueUseCase`
- Manage a list of `RawMaterialsLineDto` to be received.
- Implement validation logic for the fields in `SelectedTransitMaterialCard`.
- Implement naolon value fetching when car type and naolon description are selected.

## Verification Plan

### Manual Verification
- Deploy to a device.
- Navigate to "Receiving Raw Materials from Vendor".
- Verify the list of orders is fetched correctly.
- Select an order and verify details are loaded.
- Select a material and fill in all fields in the `SelectedTransitMaterialCard`.
- Verify Naolon is displayed after selecting car type and description.
- Add items to the list and verify they appear.
- Click "Save" and verify the `ReceiveRawMaterialsUseCase` is called with the correct data.
