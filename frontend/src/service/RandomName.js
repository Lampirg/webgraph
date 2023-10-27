class RandomName {
    names = [
        "R2-D2",
        "Yoda",
        "Anakin Skywalker",
        "Jar Jar Binks",
        "Leia Organa",
        "Finis Valorum",
        "Boba Fett",
        "Chewbacca",
        "Han Solo"
    ];

    getRandom() {
        return this.names[Math.floor(Math.random() * this.names.length)];
    }
}

export default new RandomName();