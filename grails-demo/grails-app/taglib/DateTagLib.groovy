class DateTagLib {
  def thisYear = {
    out << Calendar.getInstance().get(Calendar.YEAR)
  }
  
  def copyright = { attrs, body ->
    out << "<div id='copyright'>"
    out << "&copy; ${attrs['startYear']} - ${thisYear()}, ${body()}"
    out << "</div>"
  }
}
