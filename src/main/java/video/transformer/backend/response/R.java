package video.transformer.backend.response;


// 响应结果
public class R {

    // 状态码
    private int code;
    // 信息
    private String msg;
    // 数据
    private Object data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public R() {
    }

    public R(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public R(int code, String msg, Object data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public static R ok() {
        return new R(200, "success");
    }

    public static R ok(Object data) {
        return new R(200, "success", data);
    }

    public static R error() {
        return new R(500, "error");
    }

    public static R error(String msg) {
        return new R(500, msg);
    }

    public static R error(int code, String msg) {
        return new R(code, msg);
    }

    public static R error(int code, String msg, Object data) {
        return new R(code, msg, data);
    }
}
