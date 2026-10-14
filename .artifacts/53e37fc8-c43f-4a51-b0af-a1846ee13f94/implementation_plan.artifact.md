# Uniform Screen Styles Implementation Plan

This plan aims to unify font sizes, paddings, and button locations across all screens in the GBS Warehouse app to ensure a consistent user experience.

## User Review Required

> [!IMPORTANT]
> - **Font Sizes**: I will define a standard set of sizes (e.g., Title: 14sp, Body: 12sp, Button: 13sp). Please let me know if you have specific preferences.
> - **Primary Actions**: I will move main "Save" and "Add" buttons to a fixed bottom position where applicable, following the pattern found in `ReceivingSparePartsFromProductionDetailsScreen`.
> - **Paddings**: A standard horizontal padding of 14dp will be applied consistently.

## Proposed Changes

### 1. Centralized Styling Configuration

#### [MODIFY] [Theme.kt](file:///H:/GBSAndroidProjects/GBSWarehouseJetPackCompose/app/src/main/java/net/gbs/gbswarehouse/ui/theme/Theme.kt)
- Add `AppDimensions` object to `AppTheme` to store standard paddings and font sizes.

### 2. Update Common Components

#### [MODIFY] [Components.kt](file:///H:/GBSAndroidProjects/GBSWarehouseJetPackCompose/app/src/main/java/net/gbs/gbswarehouse/Components.kt)
- Update `TopBar`, `FullBtn`, `InfoCell`, `Section`, and other UI components to use the new `AppDimensions` for font sizes and paddings.
- Ensure `FullBtn` has a consistent default height and style.

### 3. Refactor Screens

I will refactor the following screen groups to use the unified styling and layout:

#### [MODIFY] [DetailsScreens.kt](file:///H:/GBSAndroidProjects/GBSWarehouseJetPackCompose/app/src/main/java/net/gbs/gbswarehouse/ui/receiving/receiving_spare_parts_from_production_line/details_screen/DetailsScreens.kt)
#### [MODIFY] [AuditDetailsScreen.kt](file:///H:/GBSAndroidProjects/GBSWarehouseJetPackCompose/app/src/main/java/net/gbs/gbswarehouse/ui/audit/details_screen/AuditDetailsScreen.kt)
#### [MODIFY] [IssueDetailsScreen.kt](file:///H:/GBSAndroidProjects/GBSWarehouseJetPackCompose/app/src/main/java/net/gbs/gbswarehouse/ui/issue/issuing_cost_center/details_screen/IssueDetailsScreen.kt) (and other Issue detail screens)
#### [MODIFY] [IssuingDirectTransfer.kt](file:///H:/GBSAndroidProjects/GBSWarehouseJetPackCompose/app/src/main/java/net/gbs/gbswarehouse/ui/issue/issuing_direct_transfer/IssuingDirectTransfer.kt)
#### [MODIFY] [SettingsScreen.kt](file:///H:/GBSAndroidProjects/GBSWarehouseJetPackCompose/app/src/main/java/net/gbs/gbswarehouse/ui/settings/SettingsScreen.kt)

For each screen:
- Apply `AppDimensions.screenPadding` to `LazyColumn` content padding.
- Move primary "Add" or "Save" buttons to a fixed `Box` at the `BottomCenter` with a gradient background.
- Replace hardcoded font sizes with `AppDimensions` constants.

## Verification Plan

### Automated Verification
- I will use `render_compose_preview` on key screens (if previews exist) to verify the visual changes.
- Ensure the project builds successfully after refactoring.

### Manual Verification
- Review the layout of each screen to ensure the bottom buttons don't overlap with list content (by checking `LazyColumn` bottom padding).
- Verify that font sizes look consistent across different components.
