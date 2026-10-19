package net.gbs.gbswarehouse

import net.gbs.gbswarehouse.domain.model.CarNumber
import net.gbs.gbswarehouse.domain.model.CarType
import net.gbs.gbswarehouse.domain.model.Company
import net.gbs.gbswarehouse.domain.model.CostCenter
import net.gbs.gbswarehouse.domain.model.LoadingVendor
import net.gbs.gbswarehouse.domain.model.MaterialBinLocation
import net.gbs.gbswarehouse.domain.model.MaterialOnHandStock
import net.gbs.gbswarehouse.domain.model.NaolonDescription
import net.gbs.gbswarehouse.domain.model.PackageType
import net.gbs.gbswarehouse.domain.model.SAPWarehouse
import net.gbs.gbswarehouse.domain.model.ValuationType
import net.gbs.gbswarehouse.domain.model.Warehouse
import net.gbs.gbswarehouse.domain.model.WorkersGroup

/**
 * The searchable dropdowns ([SearchableSpinner]) render their visible text from the same
 * string that filters the dropdown list. These builders are the single source of truth for
 * that text so the ViewModels can pre-fill a selection when a line is edited without the
 * label drifting away from the label the spinner renders.
 */
fun Warehouse.spinnerLabel(): String = "$warehouseDescription ($warehouseCode)"

fun SAPWarehouse.spinnerLabel(): String = "$warehouseDescription ($warehouseCode)"

fun WorkersGroup.spinnerLabel(): String = "$workerGroupDescription ($workerGroupCode)"

fun ValuationType.spinnerLabel(): String = type

fun LoadingVendor.spinnerLabel(): String = "$vendorName ($vendorCode)"

fun PackageType.spinnerLabel(): String = "$packTypeDescription ($packTypeCode)"

fun CarType.spinnerLabel(): String = "$description ($vehicleTypeName)"

fun NaolonDescription.spinnerLabel(): String = "$naolonDescription"

fun CarNumber.spinnerLabel(): String = "$carDescription ($carNumber)"

/**
 * Scrap uses the company / cost center pickers, which must produce exactly the same text as
 * the edit handler writes back into their search fields.
 */
fun Company.spinnerLabel(): String = "$companyDescription ($companyCode)"

fun CostCenter.spinnerLabel(): String = "$costCenterDescription ($costCenterCode)"

fun MaterialOnHandStock.batchSpinnerLabel(): String =
    "${batch ?: ""} ( ${availableQty ?: 0.0} ${batchDate?.take(10)})"

fun MaterialOnHandStock.valuationSpinnerLabel(): String =
    "${valuationType ?: ""} ( ${availableQty ?: 0.0} )"

/**
 * Stock rows are listed either as batches or as valuation types depending on how the
 * material is managed, and [net.gbs.gbswarehouse.ui.issue.OnHandStockSection] labels them
 * accordingly.
 */
fun MaterialOnHandStock.stockSpinnerLabel(): String =
    if (isBatchManaged == true) batchSpinnerLabel() else valuationSpinnerLabel()

/** Quantities are held as floats but must not show a trailing `.0` in the qty field. */
fun formatQty(quantity: Double): String =
    if (quantity % 1.0 == 0.0) quantity.toLong().toString() else quantity.toString()

fun formatQty(quantity: Float): String =
    if (quantity % 1.0f == 0.0f) quantity.toLong().toString() else quantity.toString()

/** Back to the spinner so a pre-filled selection can be replaced by another one. */
fun clearSpinnerText(current: String, selectedLabel: String): String =
    if (current == selectedLabel) "" else current

fun MaterialBinLocation.spinnerLabel(): String =
    "${storageBinCode} - ${storageBinName ?: ""} (Qty: ${availableQty ?: 0.0})"
