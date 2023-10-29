import http from "../http-common";

class StarWarsService {
    get(name) {
        return http.get(`/info/same-residents?name=${name}`);
    }
    getAll() {
        return http.get(`/info/all`);
    }
}

export default new StarWarsService();