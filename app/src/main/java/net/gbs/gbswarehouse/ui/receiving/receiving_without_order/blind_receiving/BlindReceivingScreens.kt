package net.gbs.gbswarehouse.ui.receiving.receiving_without_order.blind_receiving

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import net.gbs.gbswarehouse.*
import net.gbs.gbswarehouse.R
import net.gbs.gbswarehouse.ui.theme.AppTheme

@Composable
fun BlindReceivingScreen(
    navController: NavController,
) {
    val viewModel: BlindReceivingViewModel = hiltViewModel()
    val colors = AppTheme.colors
    val accent = colors.purple

    Box(modifier = Modifier.fillMaxSize().background(colors.bg)) {
        Column(Modifier.fillMaxSize()) {
            TopBar(
                title = stringResource(R.string.receiving),
                subtitle = stringResource(R.string.blind_receiving),
                accent = accent, onBack = {
                    navController.popBackStack()
                }
            )
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 14.dp),
                contentPadding = PaddingValues(bottom = 32.dp)
            ) {
                item {
                    Spacer(Modifier.height(12.dp))
                    Section(label = stringResource(R.string.material_data_manual_entry), accent = accent) {
                        // Info hint
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(accent.copy(alpha = 0.1f))
                                .border(1.dp, accent.copy(0.15f), RoundedCornerShape(8.dp))
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text("ℹ️", fontSize = 13.sp)
                            Text(
                                stringResource(R.string.no_purchase_order_hint),
                                color = accent, fontSize = 11.sp, lineHeight = 16.sp,
                                modifier = Modifier.weight(1f)
                            )
                        }
                        Spacer(Modifier.height(12.dp))

                        // Material code input / search
                        Text(stringResource(R.string.material_code_optional), color = colors.muted, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
                        Spacer(Modifier.height(4.dp))
                        Field(
                            label = "",
                            hint = stringResource(R.string.search_material_code_hint),
                            value = viewModel.materialCodeInput,
                            onValueChange = { viewModel.onMaterialCodeChanged(it) }
                        )

                        // Searched materials dropdown list if searching
                        if (viewModel.searchedMaterials.isNotEmpty()) {
                            Spacer(Modifier.height(6.dp))
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(colors.card, RoundedCornerShape(8.dp))
                                    .border(1.dp, colors.border2, RoundedCornerShape(8.dp))
                                    .padding(8.dp)
                            ) {
                                Text(stringResource(R.string.select_item), color = colors.muted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                Spacer(Modifier.height(4.dp))
                                viewModel.searchedMaterials.forEach { mat ->
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(colors.dark)
                                            .clickable { viewModel.selectMaterial(mat) }
                                            .padding(10.dp)
                                    ) {
                                        Text("${mat.materialCode} - ${mat.materialDescription} (${mat.uom})", color = colors.text, fontSize = 12.sp)
                                    }
                                    Spacer(Modifier.height(4.dp))
                                }
                            }
                        }

                        Spacer(Modifier.height(10.dp))
                        Field(
                            stringResource(
                                if (viewModel.materialCodeInput.isBlank())
                                    R.string.material_description_required
                                else R.string.material_description_optional
                            ),
                            stringResource(R.string.enter_material_description),
                            viewModel.materialDescription,
                            { viewModel.materialDescription = it },
                            enabled = viewModel.selectedMaterial == null
                        )

                        Spacer(Modifier.height(10.dp))
                        Field(
                            stringResource(R.string.vendor_optional),
                            stringResource(R.string.enter_vendor_name),
                            viewModel.vendorName,
                            { viewModel.vendorName = it }
                        )

                        Spacer(Modifier.height(10.dp))
                        Text(stringResource(R.string.plant_required), color = colors.muted, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
                        Spacer(Modifier.height(4.dp))
                        SearchableSpinner(
                            hint = stringResource(R.string.select_plant),
                            items = viewModel.plantsList,
                            itemLabel = { "${it.description} (${it.code})" },
                            onItemSelected = { viewModel.selectPlant(it) },
                            searchText = viewModel.selectedPlant?.let { "${it.description} (${it.code})" } ?: ""
                        )

                        Spacer(Modifier.height(10.dp))
                        Text(stringResource(R.string.warehouse_required), color = colors.muted, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
                        Spacer(Modifier.height(4.dp))
                        SearchableSpinner(
                            hint = stringResource(R.string.select_warehouse),
                            items = viewModel.warehousesList,
                            itemLabel = { "${it.warehouseDescription} (${it.warehouseCode})" },
                            onItemSelected = { viewModel.selectWarehouse(it) },
                            searchText = viewModel.selectedWarehouse?.let { "${it.warehouseDescription} (${it.warehouseCode})" } ?: ""
                        )

                        Spacer(Modifier.height(10.dp))
                        Text(stringResource(R.string.workers_group_optional), color = colors.muted, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
                        Spacer(Modifier.height(4.dp))
                        WorkersGroupsSpinner(
                            hint = stringResource(R.string.select_workers_group),
                            items = viewModel.workersGroupsList,
                            onItemSelected = { viewModel.selectWorkersGroup(it) },
                            accent = accent,
                            searchText = viewModel.workerGroupSearchText,
                            onSearchTextChanged = { viewModel.onWorkersGroupSearchTextChanged(it) }
                        )

                        Spacer(Modifier.height(10.dp))
                        Text(stringResource(R.string.quantity_and_uom), color = colors.muted, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
                        Spacer(Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Field(
                                label = "",
                                hint = stringResource(R.string.enter_quantity),
                                value = viewModel.quantity,
                                onValueChange = { viewModel.quantity = it },
                                isNumber = true,
                                withLabel = false,
                                modifier = Modifier.weight(1.5f)
                            )
                            if (viewModel.selectedMaterial == null) {
                                SearchableSpinner(
                                    hint = stringResource(R.string.select_or_enter_uom),
                                    items = viewModel.uomsList,
                                    itemLabel = { it.spinnerLabel() },
                                    onItemSelected = { viewModel.selectUom(it) },
                                    searchText = viewModel.uomInput,
                                    onSearchTextChanged = { viewModel.onUomInputChanged(it) },
                                    modifier = Modifier.weight(1f)
                                )
                            } else {
                                Field(
                                    label = "",
                                    hint = stringResource(R.string.uom),
                                    value = viewModel.uomInput,
                                    onValueChange = {},
                                    withLabel = false,
                                    enabled = false,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }

                        Spacer(Modifier.height(10.dp))
                        Text(stringResource(R.string.quality_decision_optional), color = colors.muted, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
                        Spacer(Modifier.height(4.dp))
                        QualityDecisionSpinner(
                            hint = stringResource(R.string.select_quality_decision),
                            items = viewModel.qualityDecisionsList,
                            onItemSelected = { viewModel.selectQualityDecision(it) }
                        )

                        Spacer(Modifier.height(10.dp))
                        Field(
                            stringResource(R.string.purchasing_officer_optional),
                            stringResource(R.string.enter_purchasing_officer_name),
                            viewModel.purchasingOfficer,
                            { viewModel.purchasingOfficer = it }
                        )

                        Spacer(Modifier.height(20.dp))

                        FullBtn(
                            label = stringResource(R.string.save_receiving),
                            color = accent,
                            onClick = { viewModel.saveBlindReceive() },
                            saving = viewModel.isSaving,
                            enabled = viewModel.isValid
                        )
                    }
                }
            }
        }

        if (viewModel.successResult != null) {
            SuccessDialog(
                accent = accent,
                message = viewModel.successResult?.text ?: stringResource(R.string.success_save),
                onClose = {
                    viewModel.successResult = null
                    viewModel.isSaved = false
                    viewModel.clearAllData()
                }
            )
        }

        if (viewModel.errorMessage != null) {
            ErrorDialog(
                accent = colors.danger,
                message = viewModel.errorMessage!!.text,
                onClose = {
                    viewModel.errorMessage = null
                }
            )
        }
    }
}
