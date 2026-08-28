# Clear Material Data and Fields after Adding to List

Ensure that all material card data and input fields are cleared when a material is added to the lines list across all relevant screens in the application.

## User Review Required

> [!IMPORTANT]
> This change will clear all input fields (Quantity, Warehouse, Valuation Type, Batch, Worker Group, etc.) immediately after clicking the "Add" button. Users will need to re-select these fields for the next item.

## Proposed Changes

I will update the `addItem` (or equivalent) function in the ViewModels of all relevant screens to ensure a complete reset of the material entry state.

### Receiving ViewModels

#### [MODIFY] [ReceivingSparePartsFromProductionDetailsViewModel.kt](file:///H:/GBSAndroidProjects/GBSWarehouseJetPackCompose/app/src/main/java/net/gbs/gbswarehouse/ui/receiving/receiving_spare_parts_from_production/details_screen/ReceivingSparePartsFromProductionDetailsViewModel.kt)
Update `addItem` to clear all selection and input fields.

#### [MODIFY] [ReceivingPackagingMaterialsFromVendorDetailsViewModel.kt](file:///H:/GBSAndroidProjects/GBSWarehouseJetPackCompose/app/src/main/java/net/gbs/gbswarehouse/ui/receiving/receiving_packaging_materials_from_vendor/details_screen/ReceivingPackagingMaterialsFromVendorDetailsViewModel.kt)
Update `addItem` to clear all selection and input fields.

#### [MODIFY] [ReceivingPoultryFromVendorDetailsViewModel.kt](file:///H:/GBSAndroidProjects/GBSWarehouseJetPackCompose/app/src/main/java/net/gbs/gbswarehouse/ui/receiving/receiving_poultry_from_vendor/details_screen/ReceivingPoultryFromVendorDetailsViewModel.kt)
Update `addItem` to clear all selection and input fields (including Naolon, worker groups, car types, etc.).

#### [MODIFY] [DetailsViewModel.kt (Raw Materials)](file:///H:/GBSAndroidProjects/GBSWarehouseJetPackCompose/app/src/main/java/net/gbs/gbswarehouse/ui/receiving/receiving_raw_materials_from_vendor/details_screen/DetailsViewModel.kt)
Update `addItem` to call `clearMaterial()` or manually clear all fields.

#### [MODIFY] [DetailsViewModel.kt (Product from Branch)](file:///H:/GBSAndroidProjects/GBSWarehouseJetPackCompose/app/src/main/java/net/gbs/gbswarehouse/ui/receiving/receiving_product_from_branch/details_screen/DetailsViewModel.kt)
Update `addItem` to clear all selection and input fields.

#### [MODIFY] [ReceivingFinishedGoodsFeedDetailsViewModel.kt](file:///H:/GBSAndroidProjects/GBSWarehouseJetPackCompose/app/src/main/java/net/gbs/gbswarehouse/ui/receiving/receiving_product_from_production_line/details_screen/ReceivingFinishedGoodsFeedDetailsViewModel.kt)
Update `addItem` to clear all selection and input fields.

#### [MODIFY] [ReceivingFinishedGoodsPlasticDetailsViewModel.kt](file:///H:/GBSAndroidProjects/GBSWarehouseJetPackCompose/app/src/main/java/net/gbs/gbswarehouse/ui/receiving/receiving_product_from_production_line_plastic/details_screen/ReceivingFinishedGoodsPlasticDetailsViewModel.kt)
Update `addItem` to clear all selection and input fields.

#### [MODIFY] [ReceivingSparePartsFromVendorDetailsViewModel.kt](file:///H:/GBSAndroidProjects/GBSWarehouseJetPackCompose/app/src/main/java/net/gbs/gbswarehouse/ui/receiving/receiving_spare_parts_from_vendor/details_screen/ReceivingSparePartsFromVendorDetailsViewModel.kt)
Update `addItem` to clear all selection and input fields.

### Issuing ViewModels

#### [MODIFY] [IssueDetailsViewModel.kt](file:///H:/GBSAndroidProjects/GBSWarehouseJetPackCompose/app/src/main/java/net/gbs/gbswarehouse/ui/issue/issuing_cost_center/details_screen/IssueDetailsViewModel.kt)
Update `addLine` to clear the bin selection fields in addition to material fields if desired, or ensure `clearMaterial` is comprehensive.

## Verification Plan

### Automated Tests
- Build the project to ensure no syntax errors.

### Manual Verification
- Deploy the app.
- Navigate to each of the modified screens.
- Add an item to the list.
- Verify that all input fields (Quantity, Warehouse, etc.) are cleared.
- Verify that the material card is dismissed.
