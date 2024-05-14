package TestProtobuf;

import com.pbftest.test.PersonBean;

import java.io.ByteArrayInputStream;
import java.io.IOException;

public class SimulatedClientServer {

    // 模拟服务器
    public static void Server(ByteArrayInputStream input){
        // 从输入流中解析出 Person 对象
        try {
            PersonBean.Person person = PersonBean.Person.parseFrom(input);
            if (person != null) {
                System.out.println("服务端收到的数据:\n" + person.toString());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // 模拟客户端
    public static byte[] Client() {
        // 构造一个 Person 对象
        PersonBean.Person person = PersonBean.Person.newBuilder()
                .setName("zhangsan")
                .setAge(18)
                .setGender("male")
                .build();


        long pbfStartTime = System.currentTimeMillis();

        // 将 Person 对象序列化为字节数组
        byte[] serializedPerson = person.toByteArray();

        long pbfEndTime = System.currentTimeMillis();
        long pbfSerializationTime = pbfEndTime - pbfStartTime;
        System.out.println("序列化所耗时间" + pbfSerializationTime + " ms");
        return serializedPerson;
    }

    public static void main(String[] args){
        // 模拟客户端发送数据
        byte[] serializedPerson = Client();

        // 使用字节数组输入流模拟网络传输
        ByteArrayInputStream bais = new ByteArrayInputStream(serializedPerson);

        // 模拟服务器接收数据
        Server(bais);

        // 因为使用了字节数组输入流，所以不需要关闭它
        // 如果是真实的网络流，则在处理完毕后需要关闭
    }
}

