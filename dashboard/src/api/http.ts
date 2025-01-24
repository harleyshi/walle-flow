import axios from "axios";
import type { AxiosInstance, AxiosRequestConfig } from "axios";
import { ElMessage } from "element-plus";
import saveAs from "file-saver";

type TAxiosOption = {
    timeout: number;
    baseURL: string;
}
 
const config: TAxiosOption = {
    timeout: 5000,
    baseURL: "http://127.0.0.1:7071"
}
 
class Http {
    service;
    constructor(config: TAxiosOption) {
        this.service = axios.create(config)
    }

    /* GET 方法 */
    get<T>(url: string, params?: object, _object = {}): Promise<any> {
        return this.service.get(url, { params, ..._object })
    }
    /* POST 方法 */
    post<T>(url: string, params?: object, _object = {}): Promise<any> {
        return this.service.post(url, params, _object)
    }
    /* PUT 方法 */
    put<T>(url: string, params?: object, _object = {}): Promise<any> {
        return this.service.put(url, params, _object)
    }
    /* DELETE 方法 */
    delete<T>(url: string, params?: any, _object = {}): Promise<any> {
        return this.service.delete(url, { params, ..._object })
    }
}

export default new Http(config)