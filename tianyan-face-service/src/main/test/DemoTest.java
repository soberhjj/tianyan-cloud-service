import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.newland.tianya.commons.base.utils.GsonUtils;
import com.newland.tianya.commons.base.utils.JsonUtils;
import com.newland.tianyan.face.FaceServiceApplication;
import com.newland.tianyan.face.utils.FaceIdSlotHelper;
import lombok.Data;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashSet;
import java.util.Set;

/**
 * @author: RojiaHuang
 * @description:
 * @date: 2021/4/8
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = FaceServiceApplication.class)
public class DemoTest {
    @Data
    static class TargetObject {
        private int count;
        private String[] array;

        public TargetObject(int count, String[] array) {
            this.count = count;
            this.array = array;
        }
    }

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
        TargetObject object = new TargetObject(0, "Anna,Beta".split(","));
        String json = JsonUtils.toJson(object);
        System.out.println(json);
    }

    @Test
    public void testSlot() {
        String source = "'0000000000'";
        String target = source.replaceAll("\'", "").trim();
        System.out.println(target);
        FaceIdSlotHelper faceIdSlotHelper = new FaceIdSlotHelper(source);
        System.out.println(faceIdSlotHelper.getIdSlotStr());
        System.out.println(faceIdSlotHelper.pollNextValidId());
        System.out.println(faceIdSlotHelper.pollNextValidId());
        System.out.println(faceIdSlotHelper.pollNextValidId());
        System.out.println(faceIdSlotHelper.getIdSlotStr());
        Set<Integer> params = new HashSet<>();
        params.add(0);
        params.add(1);
        faceIdSlotHelper.refresh(params);
        System.out.println(faceIdSlotHelper.getIdSlotStr());
    }


    @Test
    public void testJson() throws Exception {
        String json = "{\"array\":[\"Anna\",\"Beta\"],\"count\":0.22}";
        try {
            TargetObject targetObject1 = GsonUtils.fromJson(json,TargetObject.class);
            System.out.println(targetObject1.toString());
        }catch (JsonSyntaxException exception){
            System.out.println(exception.getLocalizedMessage());
        }

    }

}
