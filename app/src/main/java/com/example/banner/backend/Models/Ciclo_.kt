class Ciclo_ {
    var id: Int = 0
    var year: Int = 0
    var number: Int = 0
    var dateStart: String = ""
    var dateFinish: String = ""
    var is_active: Boolean = false

    constructor()

    constructor(id: Int, year: Int, number: Int, dateStart: String, dateFinish: String, is_active: Boolean) {
        this.id = id
        this.year = year
        this.number = number
        this.dateStart = dateStart
        this.dateFinish = dateFinish
        this.is_active = is_active
    }

    override fun toString(): String {
        return "Ciclo(id=$id, year=$year, number=$number, dateStart='$dateStart', dateFinish='$dateFinish', is_active='$is_active')"
    }
}