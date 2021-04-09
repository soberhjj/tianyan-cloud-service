import com.newland.tianya.commons.base.utils.JsonUtils;
import com.newland.tianyan.face.FaceServiceApplication;
import lombok.Builder;
import lombok.Data;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author: RojiaHuang
 * @description:
 * @date: 2021/4/8
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = FaceServiceApplication.class)
public class DemoTest {
    @Test
    public void testSharingTableKey() {
        String[] userIdList = "zhaoliying".split(",");
        for (String userId : userIdList) {
            int db = (userId.hashCode() & Integer.MAX_VALUE) % 2 + 1;
            System.out.println("db_no:" + db);
            int table = ((userId.hashCode() ^ (userId.hashCode() >>> 16)) & (16 - 1)) + 1;
            System.out.println("table_no:" + table);
            System.out.println("-----------------");
        }
    }

    @Test
    public void testJsonFormatting() {
        @Data
        class TargetObject{
            private int count;
            private String[] array;

            public TargetObject(int count, String[] array) {
                this.count = count;
                this.array = array;
            }
        }

        TargetObject object = new TargetObject(0,"Anna,Beta".split(","));
        String json = JsonUtils.toJson(object);
        System.out.println(json);
    }
}
