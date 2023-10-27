import axios from "axios";

export default axios.create({
  baseURL: "http://localhost:8080/",
  headers: {
    "Key": "USERHRDC",
    "Content-type": "application/json",
    "Access-Control-Allow-Origin": "http://localhost:8080"
  }
});
