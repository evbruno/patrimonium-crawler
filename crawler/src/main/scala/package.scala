package patrimonium

import org.jsoup.select.Elements

package object crawler {

  import scala.collection.JavaConverters._

  implicit def elementsToArr(el: Elements) = el.asScala

}
