package no.ecc.vectortile;

import org.geotools.data.FeatureWriter;
import org.geotools.data.Transaction;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.geojson.feature.FeatureJSON;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.opengis.feature.GeometryAttribute;
import org.opengis.feature.Property;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.Name;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;

/**
 * @program: java-vector-tile
 * @description:
 * @author: hyy
 * @create: 2024-05-17
 **/
@SpringBootApplication
@RestController()
@RequestMapping("/tile")
@CrossOrigin
public class Test {
    public static void main(String[] args) {
        SpringApplication.run(Test.class,args);
    }
    private static final GeometryFactory geometryFactory = new GeometryFactory();

    private static final String vtContentType = "application/octet-stream";
    private GeometryFactory gf = new GeometryFactory();
//    @RequestMapping("/{z}/{x}/{y}")
//    public void getTile(@PathVariable byte z, @PathVariable int x, @PathVariable int y, HttpServletResponse response) {
//        SimpleFeatureCollection featureCollection = convertGeoJSON2SimpleFeatureCollection("src/main/resources/china.json");
//        VectorTileEncoder vtm = new VectorTileEncoder(256);
//        SimpleFeatureIterator iterator = featureCollection.features();
//        while (iterator.hasNext()) {
//            SimpleFeature simpleFeature = iterator.next();
//            List<Object> attributes = simpleFeature.getAttributes();
//            SimpleFeatureType featureType = simpleFeature.getFeatureType();
//            List<AttributeDescriptor> attributeDescriptors = featureType.getAttributeDescriptors();
//            HashMap<String, Object> map = new HashMap<>();
//            for (int i = 0; i < attributes.size(); i++) {
//                AttributeDescriptor attributeDescriptor = attributeDescriptors.get(i);
//                map.put(String.valueOf(attributeDescriptor.getName()),attributes.get(i));
//            }
//            vtm.addFeature("layer",map,(org.locationtech.jts.geom.Geometry) simpleFeature.getDefaultGeometry());
//        }
//        iterator.close();
//
//        byte[] bytes = vtm.encode();
//        exportByte(bytes, vtContentType, response);
//    }

    @RequestMapping("/{z}/{x}/{y}")
    public void getTile(@PathVariable byte z, @PathVariable int x, @PathVariable int y, HttpServletResponse response) {
        //构造一个MvtBuilder对象
        MvtBuilder mvtBuilder = new MvtBuilder(z, x, y, geometryFactory);
        MvtLayer layer = mvtBuilder.getOrCreateLayer("省区域");
        SimpleFeatureCollection featureCollection = convertGeoJSON2SimpleFeatureCollection("src/main/resources/china.json");
        SimpleFeatureIterator iterator = featureCollection.features();
        while (iterator.hasNext()) {
            SimpleFeature simpleFeature = iterator.next();
            List<Object> attributes = simpleFeature.getAttributes();
            SimpleFeatureType featureType = simpleFeature.getFeatureType();
            List<AttributeDescriptor> attributeDescriptors = featureType.getAttributeDescriptors();
            HashMap<String, Object> map = new HashMap<>();
            for (int i = 0; i < attributes.size(); i++) {
                AttributeDescriptor attributeDescriptor = attributeDescriptors.get(i);
                map.put(String.valueOf(attributeDescriptor.getName()),attributes.get(i));
            }
//            vtm.addFeature("layer",map,(org.locationtech.jts.geom.Geometry) simpleFeature.getDefaultGeometry());
            Geometry geometry = (org.locationtech.jts.geom.Geometry) simpleFeature.getDefaultGeometry();

            if (mvtBuilder.getBbox().envIntersects(geometry)) {
                Feature feature = new Feature(geometry, map);
                layer.addFeature(feature);
            }
        }
        iterator.close();

        //数据添加完毕，转为
        byte[] bytes = mvtBuilder.toBytes();
        exportByte(bytes, vtContentType, response);
    }

    //将bytes写进HttpServletResponse
    private void exportByte(byte[] bytes, String contentType, HttpServletResponse response) {
        response.setContentType(contentType);
        try (OutputStream os = response.getOutputStream()) {
            os.write(bytes);
            os.flush();
        } catch (org.apache.catalina.connector.ClientAbortException e) {
            //地图移动时客户端主动取消， 产生异常"你的主机中的软件中止了一个已建立的连接"，无需处理
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * @param jsonFilePath GeoJSON 文件路径
     * @return FeatureCollection 要素集合
     * @description GeoJSON文件转FeatureCollection
     * @date 2024-02-12
     * @author hyy
     **/
    public static FeatureCollection convertGeoJSON2FeatureCollection(String jsonFilePath) {
        FeatureJSON featureJSON = new FeatureJSON();
        try {
            FileInputStream fileInputStream = new FileInputStream(jsonFilePath);
            try {
                return featureJSON.readFeatureCollection(fileInputStream);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static SimpleFeatureCollection convertGeoJSON2SimpleFeatureCollection(String jsonFilePath) {
        FeatureJSON featureJSON = new FeatureJSON();
        try {
            FileInputStream fileInputStream = new FileInputStream(jsonFilePath);
            try {
                return (SimpleFeatureCollection) featureJSON.readFeatureCollection(fileInputStream);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
