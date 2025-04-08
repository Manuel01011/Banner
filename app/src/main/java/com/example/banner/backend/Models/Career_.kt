class Career {
    var cod: Int = 0
    var name: String = ""
    var title: String = ""

    constructor()

    constructor(cod: Int, name: String, title: String) {
        this.cod = cod
        this.name = name
        this.title = title
    }

    override fun toString(): String {
        return "Career(cod=$cod, name='$name', title='$title')"
    }
}