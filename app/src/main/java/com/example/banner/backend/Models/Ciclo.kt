class Ciclo {
    var id: Int = 0
    var year: Int = 0
    var number: Int = 0
    var dateStart: String = ""
    var dateFinish: String = ""

    constructor()

    constructor(id: Int, year: Int, number: Int, dateStart: String, dateFinish: String) {
        this.id = id
        this.year = year
        this.number = number
        this.dateStart = dateStart
        this.dateFinish = dateFinish
    }

    override fun toString(): String {
        return "Ciclo(id=$id, year=$year, number=$number, dateStart='$dateStart', dateFinish='$dateFinish')"
    }
}