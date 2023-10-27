import http from "../http-common";

class StarWarsService {
    get(name) {
        return http.get(`/info/same-residents?name=${name}`);
    }
}

export default new StarWarsService();