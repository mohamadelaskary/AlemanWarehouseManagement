# Implementation Plan - Update Receiving Details Screen

This plan updates `ReceivingRawMaterialsFromVendorDetailsScreen` logic to:
1.  Automatically fetch the `naolon` value using `GetNaolonPerTonValueUseCase` when the user selects both `naolon description` and `car type`.
2.  Set the default `receiving quantity` to the `remaining quantity` of the selected material.

## Proposed Changes

### [app]

#### [MODIFY] [DetailsViewModel.kt](file:///H:/GBSAndroidProjects/GBSWarehouseJetPackCompose/app/src/main/java/net/gbs/gbswarehouse/ui/receiving/receiving_raw_materials_from_vendor/details_screen/DetailsViewModel.kt)

- Update `OnMaterialSelected` event handler:
    - Set the `quantity` state to the `remainingQuantity` of the selected material.
    - Call `fetchNaolonValue()` to update the naolon value if description and car type are already selected.
- Review `fetchNaolonValue()` to ensure it correctly updates `naolonValue` when triggered.

## Verification Plan

### Manual Verification
1.  Navigate to `ReceivingRawMaterialsFromVendorDetailsScreen`.
2.  Select a material.
    - Verify that the `quantity` field is pre-filled with the material's `remaining quantity`.
3.  Select a `naolon description` and a `car type`.
    - Verify that the `naolon value` is fetched and displayed correctly.
4.  Change the `naolon description` or `car type`.
    - Verify that the `naolon value` updates accordingly.
