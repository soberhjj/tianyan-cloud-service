import com.google.gson.JsonSyntaxException;
import com.newland.tianya.commons.base.utils.GsonUtils;
import com.newland.tianya.commons.base.utils.JsonUtils;
import com.newland.tianyan.face.FaceServiceApplication;
import com.newland.tianyan.face.dao.FaceMapper;
import com.newland.tianyan.face.domain.entity.FaceDO;
import com.newland.tianyan.face.utils.FaceIdSlotHelper;
import lombok.Builder;
import lombok.Data;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static com.newland.tianyan.face.constant.VerifyConstant.GROUP_ID;

/**
 * @author: RojiaHuang
 * @description:
 * @date: 2021/4/8
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = FaceServiceApplication.class)
public class DemoTest {

    @Autowired
    private FaceMapper faceMapper;

    @Data
    static class TargetObject {
        private int count;
        private String[] array;

        public TargetObject(int count, String[] array) {
            this.count = count;
            this.array = array;
        }
    }

    @Data
    @Builder
    static class FaceObject {
        private int id;
        private String path;

        public FaceObject(int id, String path) {
            this.id = id;
            this.path = path;
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


    @Test
    public void testDB() throws Exception {
        String userId = "人脸搜索中文用户";
        int db = (userId.hashCode() & Integer.MAX_VALUE) % 2 + 1;
        int table = ((userId.hashCode() ^ (userId.hashCode() >>> 16)) & (16 - 1)) + 1;
        System.out.println("db:"+db);
        System.out.println("table:"+table);
    }

    @Test
    public void deleteFace() throws Exception {
        String faceId = "1808000088080000101";
        FaceDO faceDO = new FaceDO();
        faceDO.setFaceId(faceId);
        faceMapper.delete(faceDO);
    }

    @Test
    public void checkStringMatch() throws Exception {
        String groupId = "&4AAQSkZJRgABAQAAAQABAAD_2wBDAAA0";
        System.out.println(groupId.matches(GROUP_ID));
    }

    @Test
    public void checkString() throws Exception {
        Set<String> stringSet = new HashSet<>(Arrays.asList("a,v,b,c".split(",")));
        System.out.println("test:"+stringSet.toString());
    }

    @Test
    public void getFeatures() throws Exception {
        Set<String> stringSet = new HashSet<>(Arrays.asList("a,v,b,c".split(",")));
        System.out.println("test:"+stringSet.toString());
    }

    @Test
    public void filter() throws Exception {
        Set<FaceObject> stringSetA = new HashSet<FaceObject>(){{
            add(FaceObject.builder()
                    .id(1)
                    .path("a")
                    .build());
            add(FaceObject.builder()
                    .id(2)
                    .path("b")
                    .build());
            add(FaceObject.builder()
                    .id(3)
                    .path("")
                    .build());
            add(FaceObject.builder()
                    .id(4)
                    .path("c")
                    .build());
            add(FaceObject.builder()
                    .id(5)
                    .path("d")
                    .build());
            add(FaceObject.builder()
                    .id(6)
                    .path("e")
                    .build());
            add(FaceObject.builder()
                    .id(7)
                    .path("")
                    .build());
        }};

        Set<FaceObject> stringSetB = new HashSet<FaceObject>(){{
            add(FaceObject.builder()
                    .id(1)
                    .path("a")
                    .build());
            add(FaceObject.builder()
                    .id(2)
                    .path("b")
                    .build());
            add(FaceObject.builder()
                    .id(3)
                    .path("")
                    .build());
            add(FaceObject.builder()
                    .id(4)
                    .path("c")
                    .build());
            add(FaceObject.builder()
                    .id(5)
                    .path("d")
                    .build());
        }};

        Set<String> setA = stringSetA.stream().map(FaceObject::getPath).collect(Collectors.toSet());
        Set<String> setB = stringSetB.stream().map(FaceObject::getPath).collect(Collectors.toSet());
        setA.removeAll(setB);
//        for (String item : stringSetA) {
//            if (stringSetA.contains(item)) {
//                stringSetA.remove(item);
//            }
//        }
//
        System.out.println("test:"+setA.toString());
    }
}
