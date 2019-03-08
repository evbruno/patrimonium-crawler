import org.jsoup.select.Elements

package object crawlers {

  import scala.collection.JavaConverters._

  implicit def elementsToArr(el: Elements) = el.asScala

}
