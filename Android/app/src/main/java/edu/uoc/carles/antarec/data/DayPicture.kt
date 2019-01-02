package edu.uoc.carles.antarec.data

class DayPicture{

    var uri: String? = null
    var path: String? = null
    var date: String? = null
    private var timestamp: Long? = null

    constructor()

    constructor(uri: String, path: String, date: String, timestamp: Long){
        this.uri = uri
        this.path = path
        this.date = date
        this.timestamp = timestamp
    }

}