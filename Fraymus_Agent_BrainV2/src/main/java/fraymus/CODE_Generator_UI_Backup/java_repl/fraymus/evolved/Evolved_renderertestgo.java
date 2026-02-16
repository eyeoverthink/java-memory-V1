package renderers;

import java.util.function.BiFunction;
import java.util.function.Function;
import javax.swing.*;
import java.awt.*;

public class Evolved_renderertestgo {

    public static class mockRenderer implements Renderer {
        @Override
        public String render(api.Message[] msgs, api.Tool[] tools, api.ThinkValue think) {
            StringBuilder sb = new StringBuilder();
            for (api.Message msg : msgs) {
                sb.append(msg.getMsg());
            }
            return sb.toString();
        }
    }

    public interface Renderer {
        String render(api.Message[] msgs, api.Tool[] tools, api.ThinkValue think);
    }

    public static class api {
        public static class Message {
            private String msg;

            public String getMsg() {
                return msg;
            }

            public void setMsg(String msg) {
                this.msg = msg;
            }
        }

        public static class Tool {
        }

        public static class ThinkValue {
        }
    }
}