package TestProtobuf;

import com.google.protobuf.ByteString;
import com.pbftest.test.PersonBean;

import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class ClientServer {
    public static class Server {
        public static void main(String[] args) throws Exception {
            ServerSocket ss = new ServerSocket(9999);
            System.out.println("server started...");
            Socket socket = ss.accept();
            System.out.println("a client connected!");
            //从输入流中解析出Person对象
            PersonBean.Person person = PersonBean.Person.parseFrom(ByteString.readFrom(socket.getInputStream()));
            if(person != null) {
                System.out.println("server received data:\n" + person.toString());
            }
        }
    }

    public static class Client {
        public static void main(String[] args) throws Exception {
            Socket socket = new Socket("localhost", 9999);
            //构造一个Person对象
            PersonBean.Person person = PersonBean.Person.newBuilder().setName("zhangsan")
                    .setAge(50).setGender("male").build();
            OutputStream os = socket.getOutputStream();
            //将Person对象写到输出流中
            os.write(person.toByteArray());
            os.flush();
            os.close(); // 关闭流
            System.out.println("client send person");
        }
    }

}
