class Trip { 
  static constraints = {
    airline()
    name()
    city()
    startDate()
    endDate()
    purpose(inList:["Training", "Conference", "Consulting", "Other"])
    notes(maxSize:2000)
  }

  String name
  String city
  Date startDate
  Date endDate
  String purpose
  String notes
  Airline airline
}