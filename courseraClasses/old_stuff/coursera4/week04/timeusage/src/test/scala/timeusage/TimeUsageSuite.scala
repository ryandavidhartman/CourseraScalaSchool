package timeusage

import org.apache.spark.sql.{ColumnName, DataFrame, Row}
import org.apache.spark.sql.types.{
DoubleType,
StringType,
StructField,
StructType
}
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{BeforeAndAfterAll, FunSuite}

import scala.util.Random

@RunWith(classOf[JUnitRunner])
class TimeUsageSuite extends FunSuite with BeforeAndAfterAll {

  import TimeUsage._

  lazy val testObject = TimeUsage

  override def afterAll(): Unit = {
    spark.stop()
  }

  test("testObject can be instantiated") {
    val instantiatable = try {
      testObject
      true
    } catch {
      case _: Throwable => false
    }
    assert(instantiatable, "Can't instantiate a TimeUsage object")
  }

  test("dfSchema returns the correct StructType") {
    val testFields = List[StructField](StructField("Col1", StringType, nullable = false),
      StructField("Col2", DoubleType, nullable = false),
      StructField("Col3", DoubleType, nullable = false),
      StructField("Col4", DoubleType, nullable = false))
    val expectedStructure = StructType(testFields)

    val schema = testObject.dfSchema(List("Col1", "Col2", "Col3", "Col4"))
    assert(schema === expectedStructure)
  }

  test("row function returns the correct Row RDD") {
    val expectedRowRdd = Row("val1", 1.0, 2.0)
    val rowRdd  = testObject.row(List("val1", "1", "2"))

    assert(rowRdd === expectedRowRdd)
  }

  test("classifiedColumns") {
    import org.apache.spark.sql.functions._

    val columnNames = List("tucaseid", "gemetsta", "gtmetsta", "peeduca", "pehspnon", "ptdtrace", "teage", "telfs", "temjot", "teschenr",
      "teschlvl", "tesex", "tespempnot", "trchildnum", "trdpftpt", "trernwa", "trholiday", "trspftpt", "trsppres", "tryhhchild", "tudiaryday",
      "tufnwgtp", "tehruslt", "tuyear", "t010101", "t010102", "t010199", "t010201", "t010299", "t010301", "t010399", "t010401", "t010499",
      "t010501", "t010599", "t019999", "t020101", "t020102", "t020103", "t020104", "t020199", "t020201", "t020202", "t020203", "t020299",
      "t020301", "t020302", "t020303", "t020399", "t020401", "t020402", "t020499", "t020501", "t020502", "t020599", "t020681", "t020699",
      "t020701", "t020799", "t020801", "t020899", "t020901", "t020902", "t020903", "t020904", "t020905", "t020999", "t029999", "t030101",
      "t030102", "t030103", "t030104", "t030105", "t030108", "t030109", "t030110", "t030111", "t030112", "t030186", "t030199", "t030201",
      "t030202", "t030203", "t030204", "t030299", "t030301", "t030302", "t030303", "t030399", "t030401", "t030402", "t030403", "t030404",
      "t030405", "t030499", "t030501", "t030502", "t030503", "t030504", "t030599", "t039999", "t040101", "t040102", "t040103", "t040104",
      "t040105", "t040108", "t040109", "t040110", "t040111", "t040112", "t040186", "t040199", "t040201", "t040202", "t040203", "t040204",
      "t040299", "t040301", "t040302", "t040303", "t040399", "t040401", "t040402", "t040403", "t040404", "t040405", "t040499", "t040501",
      "t040502", "t040503", "t040504", "t040505", "t040506", "t040507", "t040508", "t040599", "t049999", "t050101", "t050102", "t050103",
      "t050189", "t050201", "t050202", "t050203", "t050204", "t050289", "t050301", "t050302", "t050303", "t050304", "t050389", "t050403",
      "t050404", "t050405", "t050481", "t050499", "t059999", "t060101", "t060102", "t060103", "t060104", "t060199", "t060201", "t060202",
      "t060203", "t060289", "t060301", "t060302", "t060303", "t060399", "t060401", "t060402", "t060403", "t060499", "t069999", "t070101",
      "t070102", "t070103", "t070104", "t070105", "t070199", "t070201", "t070299", "t070301", "t070399", "t079999", "t080101", "t080102",
      "t080199", "t080201", "t080202", "t080203", "t080299", "t080301", "t080302", "t080399", "t080401", "t080402", "t080403", "t080499",
      "t080501", "t080502", "t080599", "t080601", "t080602", "t080699", "t080701", "t080702", "t080799", "t080801", "t080899", "t089999",
      "t090101", "t090102", "t090103", "t090104", "t090199", "t090201", "t090202", "t090299", "t090301", "t090302", "t090399", "t090401",
      "t090402", "t090499", "t090501", "t090502", "t090599", "t099999", "t100101", "t100102", "t100103", "t100199", "t100201", "t100299",
      "t100381", "t100383", "t100399", "t100401", "t100499", "t109999", "t110101", "t110199", "t110281", "t110289", "t119999", "t120101",
      "t120199", "t120201", "t120202", "t120299", "t120301", "t120302", "t120303", "t120304", "t120305", "t120306", "t120307", "t120308",
      "t120309", "t120310", "t120311", "t120312", "t120313", "t120399", "t120401", "t120402", "t120403", "t120404", "t120405", "t120499",
      "t120501", "t120502", "t120503", "t120504", "t120599", "t129999", "t130101", "t130102", "t130103", "t130104", "t130105", "t130106",
      "t130107", "t130108", "t130109", "t130110", "t130111", "t130112", "t130113", "t130114", "t130115", "t130116", "t130117", "t130118",
      "t130119", "t130120", "t130121", "t130122", "t130123", "t130124", "t130125", "t130126", "t130127", "t130128", "t130129", "t130130",
      "t130131", "t130132", "t130133", "t130134", "t130135", "t130136", "t130199", "t130201", "t130202", "t130203", "t130204", "t130205",
      "t130206", "t130207", "t130208", "t130209", "t130210", "t130211", "t130212", "t130213", "t130214", "t130215", "t130216", "t130217",
      "t130218", "t130219", "t130220", "t130221", "t130222", "t130223", "t130224", "t130225", "t130226", "t130227", "t130228", "t130229",
      "t130230", "t130231", "t130232", "t130299", "t130301", "t130302", "t130399", "t130401", "t130402", "t130499", "t139999", "t140101",
      "t140102", "t140103", "t140104", "t140105", "t149999", "t150101", "t150102", "t150103", "t150104", "t150105", "t150106", "t150199",
      "t150201", "t150202", "t150203", "t150204", "t150299", "t150301", "t150302", "t150399", "t150401", "t150402", "t150499", "t150501",
      "t150599", "t150601", "t150602", "t150699", "t159989", "t160101", "t160102", "t160103", "t160104", "t160105", "t160106", "t160107",
      "t160108", "t169989", "t180101", "t180199", "t180280", "t180381", "t180382", "t180399", "t180481", "t180482", "t180499", "t180501",
      "t180502", "t180589", "t180601", "t180682", "t180699", "t180701", "t180782", "t180801", "t180802", "t180803", "t180804", "t180805",
      "t180806", "t180807", "t180899", "t180901", "t180902", "t180903", "t180904", "t180905", "t180999", "t181002", "t181081", "t181099",
      "t181101", "t181199", "t181201", "t181202", "t181204", "t181283", "t181299", "t181301", "t181302", "t181399", "t181401", "t181499",
      "t181501", "t181599", "t181601", "t181699", "t181801", "t181899", "t189999", "t500101", "t500103", "t500104", "t500105", "t500106",
      "t500107", "t509989")

    val expectedPrimaryNeeds =  List(col("t010101"), col("t010102"), col("t010199"), col("t010201"), col("t010299"), col("t010301"), col("t010399"),
      col("t010401"), col("t010499"), col("t010501"), col("t010599"), col("t019999"), col("t030101"), col("t030102"), col("t030103"), col("t030104"),
      col("t030105"), col("t030108"), col("t030109"), col("t030110"), col("t030111"), col("t030112"), col("t030186"), col("t030199"), col("t030201"),
      col("t030202"), col("t030203"), col("t030204"), col("t030299"), col("t030301"), col("t030302"), col("t030303"), col("t030399"), col("t030401"),
      col("t030402"), col("t030403"), col("t030404"), col("t030405"), col("t030499"), col("t030501"), col("t030502"), col("t030503"), col("t030504"),
      col("t030599"), col("t039999"), col("t110101"), col("t110199"), col("t110281"), col("t110289"), col("t119999"), col("t180101"), col("t180199"),
      col("t180381"), col("t180382"), col("t180399"))

    val expectedWork = List(col("t050101"), col("t050102"), col("t050103"), col("t050189"), col("t050201"), col("t050202"), col("t050203"), col("t050204"),
      col("t050289"), col("t050301"), col("t050302"), col("t050303"), col("t050304"), col("t050389"), col("t050403"), col("t050404"), col("t050405"),
      col("t050481"), col("t050499"), col("t059999"), col("t180501"), col("t180502"), col("t180589"))

    val expectedOther = List(col("t020101"), col("t020102"), col("t020103"), col("t020104"), col("t020199"), col("t020201"), col("t020202"), col("t020203"),
      col("t020299"), col("t020301"), col("t020302"), col("t020303"), col("t020399"), col("t020401"), col("t020402"), col("t020499"), col("t020501"), col("t020502"),
      col("t020599"), col("t020681"), col("t020699"), col("t020701"), col("t020799"), col("t020801"), col("t020899"), col("t020901"), col("t020902"), col("t020903"),
      col("t020904"), col("t020905"), col("t020999"), col("t029999"), col("t040101"), col("t040102"), col("t040103"), col("t040104"), col("t040105"), col("t040108"),
      col("t040109"), col("t040110"), col("t040111"), col("t040112"), col("t040186"), col("t040199"), col("t040201"), col("t040202"), col("t040203"), col("t040204"),
      col("t040299"), col("t040301"), col("t040302"), col("t040303"), col("t040399"), col("t040401"), col("t040402"), col("t040403"), col("t040404"), col("t040405"),
      col("t040499"), col("t040501"), col("t040502"), col("t040503"), col("t040504"), col("t040505"), col("t040506"), col("t040507"), col("t040508"), col("t040599"),
      col("t049999"), col("t060101"), col("t060102"), col("t060103"), col("t060104"), col("t060199"), col("t060201"), col("t060202"), col("t060203"), col("t060289"),
      col("t060301"), col("t060302"), col("t060303"), col("t060399"), col("t060401"), col("t060402"), col("t060403"), col("t060499"), col("t069999"), col("t070101"),
      col("t070102"), col("t070103"), col("t070104"), col("t070105"), col("t070199"), col("t070201"), col("t070299"), col("t070301"), col("t070399"), col("t079999"),
      col("t080101"), col("t080102"), col("t080199"), col("t080201"), col("t080202"), col("t080203"), col("t080299"), col("t080301"), col("t080302"), col("t080399"),
      col("t080401"), col("t080402"), col("t080403"), col("t080499"), col("t080501"), col("t080502"), col("t080599"), col("t080601"), col("t080602"), col("t080699"),
      col("t080701"), col("t080702"), col("t080799"), col("t080801"), col("t080899"), col("t089999"), col("t090101"), col("t090102"), col("t090103"), col("t090104"),
      col("t090199"), col("t090201"), col("t090202"), col("t090299"), col("t090301"), col("t090302"), col("t090399"), col("t090401"), col("t090402"), col("t090499"),
      col("t090501"), col("t090502"), col("t090599"), col("t099999"), col("t100101"), col("t100102"), col("t100103"), col("t100199"), col("t100201"), col("t100299"),
      col("t100381"), col("t100383"), col("t100399"), col("t100401"), col("t100499"), col("t109999"), col("t120101"), col("t120199"), col("t120201"), col("t120202"),
      col("t120299"), col("t120301"), col("t120302"), col("t120303"), col("t120304"), col("t120305"), col("t120306"), col("t120307"), col("t120308"), col("t120309"),
      col("t120310"), col("t120311"), col("t120312"), col("t120313"), col("t120399"), col("t120401"), col("t120402"), col("t120403"), col("t120404"), col("t120405"),
      col("t120499"), col("t120501"), col("t120502"), col("t120503"), col("t120504"), col("t120599"), col("t129999"), col("t130101"), col("t130102"), col("t130103"),
      col("t130104"), col("t130105"), col("t130106"), col("t130107"), col("t130108"), col("t130109"), col("t130110"), col("t130111"), col("t130112"), col("t130113"),
      col("t130114"), col("t130115"), col("t130116"), col("t130117"), col("t130118"), col("t130119"), col("t130120"), col("t130121"), col("t130122"), col("t130123"),
      col("t130124"), col("t130125"), col("t130126"), col("t130127"), col("t130128"), col("t130129"), col("t130130"), col("t130131"), col("t130132"), col("t130133"),
      col("t130134"), col("t130135"), col("t130136"), col("t130199"), col("t130201"), col("t130202"), col("t130203"), col("t130204"), col("t130205"), col("t130206"),
      col("t130207"), col("t130208"), col("t130209"), col("t130210"), col("t130211"), col("t130212"), col("t130213"), col("t130214"), col("t130215"), col("t130216"),
      col("t130217"), col("t130218"), col("t130219"), col("t130220"), col("t130221"), col("t130222"), col("t130223"), col("t130224"), col("t130225"), col("t130226"),
      col("t130227"), col("t130228"), col("t130229"), col("t130230"), col("t130231"), col("t130232"), col("t130299"), col("t130301"), col("t130302"), col("t130399"),
      col("t130401"), col("t130402"), col("t130499"), col("t139999"), col("t140101"), col("t140102"), col("t140103"), col("t140104"), col("t140105"), col("t149999"),
      col("t150101"), col("t150102"), col("t150103"), col("t150104"), col("t150105"), col("t150106"), col("t150199"), col("t150201"), col("t150202"), col("t150203"),
      col("t150204"), col("t150299"), col("t150301"), col("t150302"), col("t150399"), col("t150401"), col("t150402"), col("t150499"), col("t150501"), col("t150599"),
      col("t150601"), col("t150602"), col("t150699"), col("t159989"), col("t160101"), col("t160102"), col("t160103"), col("t160104"), col("t160105"), col("t160106"),
      col("t160107"), col("t160108"), col("t169989"), col("t180280"), col("t180481"), col("t180482"), col("t180499"), col("t180601"), col("t180682"), col("t180699"),
      col("t180701"), col("t180782"), col("t180801"), col("t180802"), col("t180803"), col("t180804"), col("t180805"), col("t180806"), col("t180807"), col("t180899"),
      col("t180901"), col("t180902"), col("t180903"), col("t180904"), col("t180905"), col("t180999"), col("t181002"), col("t181081"), col("t181099"), col("t181101"),
      col("t181199"), col("t181201"), col("t181202"), col("t181204"), col("t181283"), col("t181299"), col("t181301"), col("t181302"), col("t181399"), col("t181401"),
      col("t181499"), col("t181501"), col("t181599"), col("t181601"), col("t181699"), col("t181801"), col("t181899"), col("t189999"))

    val (p, w, o) = testObject.classifiedColumns(columnNames = columnNames)
    assert(p === expectedPrimaryNeeds)
    assert(w === expectedWork)
    assert(o === expectedOther)

  }

}
