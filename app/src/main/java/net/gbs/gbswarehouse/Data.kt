package net.gbs.gbswarehouse
import androidx.compose.ui.graphics.Color
import net.gbs.gbswarehouse.domain.model.ValuationType
import net.gbs.gbswarehouse.domain.model.Warehouse

// ── COLORS ─────────────────────────────────────────────────────────────────
// Color.kt

val GREEN   = Color(0xFF00C896)
val ORANGE  = Color(0xFFFF6224)
val YELLOW  = Color(0xFFFFB800)
val PURPLE  = Color(0xFFA78BFA)
val BLUE  = Color(0xFF023D95)


val GRAY    = Color(0xFF4A6080)
val SUCC    = Color(0xFF22C55E)
val DANGER  = Color(0xFFEF4444)





// ── DATA MODELS ─────────────────────────────────────────────────────────────
data class Order(
    val id: String,
    val info: String,
    val date: String,
    val items: Int,
    val status: String,
    val statusColor: Color,
    var material: Material? = null,
    var materials: List<Material>,
) {
    fun materialsToString(): String {
        var materialsToString = ""
        materials.forEachIndexed { index, material ->
            if (index == 0) {
                materialsToString += material.toString()

            } else if(index == materials.size - 1) {
                materialsToString += ", $material\n"
            } else {
                materialsToString += ", $material"
            }
        }
        return materialsToString
    }

}

data class Material(
    val code: String,
    val desc: String,
    val unit: String,
    val qty: Int,
    val done: Int
){
    override fun toString(): String {
        return "( $code - $desc - $qty $unit)"
    }
}

data class BatchNo(
    var id: String,
    var name: String
)

data class Bin(
    val code: String,
    val zone: String,
    val aisle: String,
    val rack: String,
    val cap: String,
    val avail: String
)

data class Operation(
    val id: String,
    val enName: String,
    val arName: String,
    val refType: String?,
    val blind: Boolean = false
)

data class OpType(
    val id: String,
    val enLabel: String,
    val label: String,
    val accent: Color,
    val icon: String,
    val hint: String
)

data class WorkersGroup (
    val id: String,
    val name: String,
)


data class QualityDecision (
    val id: String,
    val name: String,
)
data class CostCenter (
    val id: String,
    val name: String,
)
data class TransportationType (
    val id: String,
    val name: String,
)
data class DirectTransferWarehouseToType (
    val id: String,
    val name: String,
)

data class CarType (
    val id: String,
    val name: String,
)

data class CarSource (
    val id: String,
    val name: String,
)

data class LoadType (
    val id: String,
    val name: String,
)


data class ReturnReason (
    val id: String,
    val name: String,
)



data class ReturnType (
    val id: String,
    val name: String,
)

data class MovementType (
    val id: String,
    val name: String,
)

data class ProductionLine (
    val id: String,
    val name: String,
)

data class PartialIssueReason (
    val id: String,
    val name: String,
)

data class Shift (
    val id: String,
    val name: String
)

data class IssueReason (
    val id: String,
    val name: String
)

data class UsageReason(
    val id: String,
    val name: String
)

data class ScrapReason (
    val id: String,
    val name: String
)

data class TransportingVendor(
    val id: String,
    val name: String
)
data class UOM (
    val id: String,
    val name: String
)
// ── SAMPLE DATA ─────────────────────────────────────────────────────────────

val costCenters = listOf(
    CostCenter("1","مركز تكلفة 1"),
    CostCenter("2","مركز تكلفة 2"),
)
val directTransferWarehouseToTypes = listOf(
    DirectTransferWarehouseToType("1","مخزن رئيسي"),
    DirectTransferWarehouseToType("2","مخزن الانتاج")
)
val batchNos = listOf(
    BatchNo("1","batch 1"),
    BatchNo("2","batch 2"),
    BatchNo("3","batch 3"),
)
val uoms = listOf(
    UOM("1","KG"),
    UOM("2","M"),
    UOM("3","Unit"),
    UOM("4","Roll"),
)
val transportingVendors = listOf(
    TransportingVendor("1","مورد النقل 1"),
    TransportingVendor("2","مورد النقل 2"),
)
val scrapReasons = listOf(
    ScrapReason("1","سبب الاعدام 1"),
    ScrapReason("2","سبب الاعدام 2"),
)
val usageReasons = listOf(
    UsageReason("1","سبب الاستخدام 1"),
    UsageReason("2","سبب الاستخدام 2"),
)
val issueReasons = listOf(
    IssueReason("1","سبب الصرف 1"),
    IssueReason("2","سبب الصرف 2"),
)
val shifts = listOf(
    Shift("1","شيفت 1"),
    Shift("2","شيفت 2"),
)
val partialIssueReasons = listOf(
    PartialIssueReason("1","سبب الصرف الجزئي 1"),
    PartialIssueReason("2","سبب الصرف الجزئي 2"),
)
val productionLines = listOf(
    ProductionLine("1","خط انتاج 1"),
    ProductionLine("2","خط انتاج 2"),
)
val packagingLines = listOf(
    ProductionLine("1","خط تعبئة 1"),
    ProductionLine("2","خط تعبئة 2"),
)
val returnReasons = listOf(
    ReturnReason("1","سبب الارتجاع1"),
    ReturnReason("2","سبب الارتجاع2")
)
val movementTypes = listOf(
    MovementType("1","من مركز تكلفة")
)

var returnTypes = listOf(
    ReturnType("1","أمر صيانة"),
    ReturnType("2","مرتجع أمر داخلي"),
)
val transportationTypes: List<TransportationType> = listOf(
    TransportationType("1","أرض المصنع"),
    TransportationType("2","وصال")
)
val carTypes: List<CarType> = listOf(
    CarType("1","ربع نقل"),
    CarType("2","نصف نقل"),
    CarType("3","جامبو")
)

val carSources = listOf(
    CarSource("1","العاشر من رمضان"),
    CarSource("2","القاهرة"),
    CarSource("3","البحيرة"),
)

val loadTypes = listOf(
    LoadType("1","صب"),
    LoadType("2","معبأ"),
    LoadType("3","حاوية")
)

val workersGroup: List<WorkersGroup> = listOf(
    WorkersGroup("1","مجموعة 1"),
    WorkersGroup("2","مجموعة 2"),
    WorkersGroup("3","مجموعة 3"),
)

val qualityDecisions: List<QualityDecision> = listOf(
    QualityDecision("1","قرار الجودة 1 "),
    QualityDecision("2","قرار الجودة 2 "),
    QualityDecision("3","قرار الجودة 3 ")
)
val warehouses: List<Warehouse> = listOf(
    Warehouse(378,"W713","ميديترانيو","WF01"),
)
val valuationTypes = listOf(
    ValuationType("1"),
)
val MATERIALS = listOf(
    Material("MAT-10001", "قمح طري — درجة أولى",          "كجم",   500,  200),
    Material("MAT-10002", "ذرة صفراء — توريد محلي",       "كجم",   300,  300),
    Material("MAT-10003", "كسب فول الصويا المحلل",         "كجم",   150,  0),
    Material("SPA-30001", "محرك كهربائي 15 كيلوواط",       "قطعة",  2,    0),
    Material("SPA-30002", "مضخة هيدروليكية HP-200",        "قطعة",  1,    0),
    Material("MAT-20001", "كرتون تغليف 40×30×20",          "قطعة",  1000, 500),
    Material("MAT-20002", "شريط لاصق بولي — بني",          "رول",   200,  200),
    Material("PKG-40001", "أكياس بولي بروبيلين 25 كجم",   "قطعة",  5000, 2000),
)

val RAW_MATERIALS:List<Material> = listOf(
    Material("MAT-10001", "قمح طري — درجة أولى",          "كجم",   500,  200),
    Material("MAT-10002", "ذرة صفراء — توريد محلي",       "كجم",   300,  300),
)

val PRODUCT_MATERIAL = listOf(
    Material("MAT-10003", "كسب فول الصويا المحلل",         "كجم",   150,  0),
    )

val SPARE_PARTS_MATERIALS = listOf(
    Material("SPA-30001", "محرك كهربائي 15 كيلوواط",       "قطعة",  2,    0),
    Material("SPA-30002", "مضخة هيدروليكية HP-200",        "قطعة",  1,    0),
)

val PACKAGING_MATERIALS = listOf(
    Material("MAT-20001", "كرتون تغليف 40×30×20",          "قطعة",  1000, 500),
    Material("MAT-20002", "شريط لاصق بولي — بني",          "رول",   200,  200),
    Material("PKG-40001", "أكياس بولي بروبيلين 25 كجم",   "قطعة",  5000, 2000),
)

val ORDERS: Map<String, List<Order>> = mapOf(
    "PO" to listOf(
        Order("4500001234", "شركة النيل للمواد الخام",      "12/05/2026", 3, "مفتوح", GREEN, materials = RAW_MATERIALS),
        Order("4500001235", "مجموعة الدلتا للتوريد",        "11/05/2026", 5, "جزئي",  YELLOW, materials = RAW_MATERIALS),
        Order("4500001236", "شركة سيناء للتوريدات",         "10/05/2026", 2, "مفتوح", GREEN, materials = SPARE_PARTS_MATERIALS),
        Order("4500001230", "المصرية للبتروكيماويات",       "08/05/2026", 7, "جزئي",  YELLOW, materials = PACKAGING_MATERIALS),
    ),
    "PROD" to listOf(
        Order("1000012345", "خط الإنتاج 1 — وردية صباح",  "12/05/2026", 4, "جارٍ",   GREEN, materials = PRODUCT_MATERIAL),
        Order("1000012346", "خط الإنتاج 2 — وردية مساء",  "12/05/2026", 2, "جارٍ",   GREEN, materials = PRODUCT_MATERIAL),
        Order("1000012340", "خط الإنتاج 3 — وردية صباح",  "11/05/2026", 6, "مكتمل",  GRAY, materials = PRODUCT_MATERIAL),
    ),
    "MAINT" to listOf(
        Order("WO-001234", "صيانة ميكانيكية — خط 1",      "12/05/2026", 2, "مفتوح", GREEN, materials = SPARE_PARTS_MATERIALS),
        Order("WO-001235", "صيانة كهربائية — المحطة",     "10/05/2026", 1, "جزئي",  YELLOW, materials = SPARE_PARTS_MATERIALS),
    ),
    "INTERNAL" to listOf(
        Order("INT-001001", "مشروع توسعة — قسم الإنتاج",  "12/05/2026", 4, "مفتوح", GREEN, materials = PRODUCT_MATERIAL),
        Order("INT-001002", "صيانة دورية — خط 2",          "11/05/2026", 2, "مفتوح", GREEN, materials = PRODUCT_MATERIAL),
        Order("INT-001003", "أعمال بناء — المستودعات",     "10/05/2026", 5, "جزئي",  YELLOW,materials = PRODUCT_MATERIAL),
    ),
    "SO" to listOf(
        Order("SO-0045678", "شركة الإنماء للتجارة",        "12/05/2026", 3, "مفتوح", GREEN,materials = PRODUCT_MATERIAL),
        Order("SO-0045679", "مؤسسة البركة التجارية",       "11/05/2026", 5, "مفتوح", GREEN,materials = PRODUCT_MATERIAL),
    ),
    "TRANSFER" to listOf(
        Order("TR-001234", "فرع الإسكندرية ← المصنع",     "12/05/2026", 4, "مفتوح", GREEN,materials = PRODUCT_MATERIAL),
        Order("TR-001230", "فرع بورسعيد ← المصنع",        "10/05/2026", 2, "جزئي",  YELLOW,materials = PRODUCT_MATERIAL),
    ),
    "SCRAP" to listOf(
        Order("SC-001001", "إعدام مواد تالفة — مخزن A",   "11/05/2026", 2, "معلق",  YELLOW, materials = SPARE_PARTS_MATERIALS),
        Order("SC-001002", "خردة قطع غيار — مخزن B",      "10/05/2026", 3, "معلق",  YELLOW, materials = SPARE_PARTS_MATERIALS),
    ),
    "AUDIT" to listOf(
        Order  ("Aud-0012230","المخزن 1", "09-07-2026",0,"جاري", BLUE, materials = listOf())
    )
)





val BINS = listOf(
    Bin("A-01-R1", "منطقة أ", "ممر 01", "R1", "500 كجم",  "350 كجم"),
    Bin("A-01-R2", "منطقة أ", "ممر 01", "R2", "500 كجم",  "500 كجم"),
    Bin("B-02-R1", "منطقة ب", "ممر 02", "R1", "1000 كجم", "750 كجم"),
    Bin("C-03-R3", "منطقة ج", "ممر 03", "R3", "200 قطعة", "150 قطعة"),
)

val RECV_OPS = listOf(
    Operation("recv-raw", enName = "Raw materials from vendor",        "مواد خام من مورد",                  "PO"),
    Operation("recv-spare",enName = "Spare parts from vendor",      "قطع غيار من مورد",                  "PO"),
    Operation("recv-pkg",  enName = "Packaging materials from vendor",      "مواد تعبئة وتغليف من مورد",         "PO"),
    Operation("recv-blind", enName = "Receiving without purchase order",     "استلام بدون أمر شراء",                       null, blind = true),
    Operation("recv-prod-fg",enName = "Finished goods ()",    "منتج تام من الإنتاج(علف)",              "PROD"),
    Operation("recv-prod-fg-plastic", enName = "Finished goods (Plastic)",   "منتج تام من الإنتاج(بلاستيك)",              "PROD"),
    Operation("recv-prod-spare", enName = "Return spare parts to warehouse","ارتجاع قطع غيار للمخزن",      "PROD"),
    Operation("recv-branch",  enName = "Warehouse transfers between Locations",   "تحويلات مخزنية بين المواقع",              "TRANSFER"),
    Operation("recv-return",  enName = "Returns from customers",   "استلام منتج تام مرتجع من العميل",  "SO"),
)

val ISSUE_OPS = listOf(
    Operation("issue-raw",enName = "Transfers to production warehouses",      "تحويلات إلى مخازن الانتاج",             "PROD"),
    Operation("issue-spare",   enName = "Spare parts fo maintenance", "قطع غيار للصيانة",                 "MAINT"),
    Operation("issue-internal",enName = "Internal order", "أمر داخلي",                         "INTERNAL"),
    Operation("issue-sales",  enName = "Finished goods for sales or shipping ()",  "منتج تام للبيع أو الشحن (علف)",          "SO"),
    Operation("issue-sales_plastic",enName = "Finished goods for sales or shipping (Plastic)", "منتج تام للبيع أو الشحن (بلاستيك)",          "SO"),
    Operation("issue-cost-center", enName = "Direct issuing on cost center",   "صرف مباشر علي مركز تكلفة",          "RESERVATION"),
    Operation("issue-direct-transfer",enName = "Direct transfers",    "تحويلات مباشرة",          "SO"),
    Operation("issue-sub", enName = "For vendors (subcontracting)",     "للمورد (تصنيع لدى الغير)",         "PO"),
    Operation("issue-scrap",   enName = "Scrap", "هالك أو إعدام أو خردة",            "SCRAP"),
)

val OP_TYPES = listOf(
    OpType("receive",enLabel = "Receiving", "استلام",          GREEN,  "↓", "تسجيل الكمية الواردة"),
    OpType("quality", enLabel = "Quality","مراجعة الجودة",   PURPLE, "◎", "فحص وقبول / رفض"),
    OpType("putaway", enLabel = "Put away","تسكين",           YELLOW, "⌘", "تحديد موقع التخزين"),
)

val OP_TYPES_WITHOUT_INSPECTION = listOf(
    OpType("receive",enLabel = "Receiving", "استلام",          GREEN,  "↓", "تسجيل الكمية الواردة"),
    OpType("putaway",enLabel = "Put away", "تسكين",           YELLOW, "⌘", "تحديد موقع التخزين"),
)