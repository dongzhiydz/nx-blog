package com.example.utils.generator;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.db.Db;
import cn.hutool.db.Entity;
import com.alibaba.druid.pool.DruidDataSource;
import org.apache.commons.compress.utils.Lists;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * mybatis代码生成器
 * @author 微信公众号：Java开发宝典
 * @date 2021-6-25
 */
public class MybatisCodeGenerator {

    /**
     * BASE_FILE_PATH：文件存储路径
     */
    private static final String BASE_FILE_PATH = System.getProperty("user.dir") + "/src/main/java/com/example/";

    private static final String SPACE4 = "    ";
    private static final String SPACE6 = SPACE4 + "  ";
    private static final String SPACE8 = SPACE4 + SPACE4;
    private static final String SPACE12 = SPACE4 + SPACE8;

    //-----------------------------------------------------------------------------//
    /**
     * 数据库实体基本信息
     *
     * SCHEMA_NAME：数据库名称（必填）
     * TABLE：第一个参数填数据库表名（必填），第二个参数填对应的实体类名（选填）
     * MODEL_NAME：页面展示的模块名称（必填）
     *
     * DATA_SOURCE：数据源
     * DB_NAME：数据库用户名（必填）
     * DB_PASS：数据库密码（必填）
     * DB_PORT：数据库端口号（必填）
     */
    private static final String SCHEMA_NAME = "nx-blog";
    private static final String[] TABLE = {"t_link", "Link"};
    private static final String MODEL_NAME = "资源";

    private static final DruidDataSource DATA_SOURCE = new DruidDataSource();
    private static final String DB_NAME = "root";
    private static final String DB_PASS = "123456";
    private static final String DB_PORT = "3306";

    //-----------------------------------------------------------------------------//

    /**
     * “预约/提交 审核模型“初始化部分
     *
     * IS_RESERVE：预约审核模块开关（true：是，false：不是）
     * IS_SUBMIT：提交审核模块开关（true：是，false：不是）
     * IS_PUBLISH_UPLOAD：发布带文件上传开关（true：带文件上传，false：不带文件上传）
     * IS_RESERVE_UPLOAD：预约带文件上传开关（true：带文件上传，false：不带文件上传）
     */
    private static final boolean IS_RESERVE = false;
    private static final boolean IS_SUBMIT = false;
    private static final boolean IS_PUBLISH_UPLOAD = false;
    private static final boolean IS_RESERVE_UPLOAD = false;

    static {
        // 必填
        DATA_SOURCE.setUrl("jdbc:mysql://localhost:" + DB_PORT + "/" + SCHEMA_NAME + "?useUnicode=true&characterEncoding=utf-8&allowMultiQueries=true&useSSL=false&serverTimezone=GMT%2b8");
        DATA_SOURCE.setUsername(DB_NAME);
        DATA_SOURCE.setPassword(DB_PASS);
    }

    /**
     * 代码生成主函数入口
     */
    public static void main(String[] args) throws Exception {

        if (StrUtil.isBlank(TABLE[0])) {
            System.err.println("请完善配置");
            return;
        }
        // 获取实体名称
        String entityName = getEntityName();
        // 创建entity
        createEntity(TABLE[0], entityName);
        // 创建mapper
        createMapper(entityName);
        // 创建service
        createService(entityName);
        // 创建controller
        createController(entityName);
        // 创建xml
        createXml(entityName);
        // 创建html
        createVueHtml(entityName, TABLE[0]);
    }

    /**
     * 获取数据库对象
     *
     * @param tableName 表名
     * @return 表所有字段信息
     * @throws SQLException SQLException
     */
    private static List<TableColumn> getTableColumns(String tableName) throws SQLException {
        String sql = "SELECT table_name,column_name,data_type, column_comment FROM information_schema.COLUMNS WHERE table_schema = ? and table_name = ?";
        List<Entity> user = Db.use(DATA_SOURCE).query(sql, SCHEMA_NAME, tableName);
        List<TableColumn> columnList = Lists.newArrayList();
        for (Entity entity : user) {
            TableColumn tableColumn = new TableColumn();
            tableColumn.setTableName(entity.getStr("table_name"));
            tableColumn.setColumnName(entity.getStr("column_name"));
            tableColumn.setDataType(convertDataType(entity.getStr("data_type")));
            tableColumn.setColumnComment(entity.getStr("column_comment"));
            columnList.add(tableColumn);
        }
        return columnList;
    }

    /**
     * 生成entity
     */
    private static void createEntity(String tableName, String entityName) throws SQLException {
        List<TableColumn> columnList = getTableColumns(tableName);

        // 1. 字段
        StringBuilder fieldBodyBuild = StrUtil.builder()
                .append(SPACE4).append("/**\n")
                .append(SPACE6).append("*").append(" 主键\n")
                .append(SPACE6).append("*/\n")
                .append(SPACE4).append("@TableId(value = \"id\", type = IdType.AUTO)\n")
                .append(SPACE4).append("private Long id;\n\n");

        // 2. GET、SET方法
        StringBuilder getSetBodyBuild = StrUtil.builder()
                .append(SPACE4).append("public Long getId() {\n")
                .append(SPACE6).append("return id;\n")
                .append(SPACE4).append("}").append("\n\n")
                .append(SPACE4).append("public void setId(Long id) {\n")
                .append(SPACE6).append("this.id = id;\n")
                .append(SPACE4).append("}\n\n");

        for (TableColumn tableColumn : columnList) {
            String columnName = tableColumn.getColumnName();
            if (!"id".equals(columnName)) {
                // 生成注释
                if (StrUtil.isNotBlank(tableColumn.getColumnComment())) {
                    fieldBodyBuild
                            .append(SPACE4).append("/**\n")
                            .append(SPACE6).append("* ").append(tableColumn.getColumnComment()).append(" \n")
                            .append(SPACE6).append("*/\n");
                }
                // 生成字段
                fieldBodyBuild.append(SPACE4).append("private ").append(tableColumn.getDataType()).append(" ").append(StrUtil.toCamelCase(columnName)).append(";\n\n");
                // 生成GET/SET方法
                getSetBodyBuild
                        .append(SPACE4).append("public ").append(tableColumn.getDataType()).append(" get").append(toCamelFirstUpper(columnName)).append("() {\n")
                        .append(SPACE6).append("return ").append(columnName).append(";\n")
                        .append(SPACE4).append("}").append("\n")
                        .append(SPACE4).append("public void set").append(toCamelFirstUpper(columnName)).append("(").append(tableColumn.getDataType()).append(" ").append(columnName).append(") {\n")
                        .append(SPACE6).append("this.").append(columnName).append(" = ").append(columnName).append(";\n")
                        .append(SPACE4).append("}\n\n");
            }
        }

        Map<String, Object> map = new HashMap<>(1);
        map.put("tableName", tableName);
        map.put("entityName", entityName);
        map.put("body", fieldBodyBuild.append(getSetBodyBuild).toString());
        String format = StrUtil.format(FileUtil.readUtf8String(BASE_FILE_PATH + "/utils/generator/template/entity.template"), map);
        FileUtil.writeString(format, BASE_FILE_PATH + "/entity/" + entityName + ".java", "UTF-8");
        System.out.println(entityName + "Entity生成成功！");
    }

    /**
     * 生成mapper
     */
    private static void createMapper(String entityName) {
        Map<String, Object> map = new HashMap<>(1);
        map.put("entityName", entityName);
        String format = StrUtil.format(FileUtil.readUtf8String(BASE_FILE_PATH + "/utils/generator/template/mapper.template"), map);
        FileUtil.writeString(format, BASE_FILE_PATH + "/mapper/" + entityName + "Mapper" + ".java", "UTF-8");
        System.out.println(entityName + "Mapper生成成功！");
    }

    /**
     * 生成service
     */
    private static void createService(String entityName) {
        String lowerName = entityName.substring(0, 1).toLowerCase() + entityName.substring(1);

        Map<String, Object> map = new HashMap<>(2);
        map.put("entityName", entityName);
        map.put("lowerName", lowerName);
        String format = StrUtil.format(FileUtil.readUtf8String(BASE_FILE_PATH + "/utils/generator/template/service.template"), map);
        FileUtil.writeString(format, BASE_FILE_PATH + "/service/" + entityName + "Service" + ".java", "UTF-8");
        System.out.println(entityName + "Service生成成功！");
    }

    /**
     * 生成controller
     */
    private static void createController(String entityName) {

        // 预约审核模型controller是现成的
        if (IS_RESERVE) {
            FileUtil.copyContent(FileUtil.file(BASE_FILE_PATH + "/utils/generator/template/verify_controller.template"),
                    FileUtil.file(BASE_FILE_PATH + "/controller/VerifyController" + ".java"), true);
            System.out.println(entityName + "Controller生成成功！");
            return;
        }

        // 通用模块
        String lowerEntityName = entityName.substring(0, 1).toLowerCase() + entityName.substring(1);

        Map<String, Object> map = new HashMap<>(2);
        map.put("entityName", entityName);
        map.put("lowerName", lowerEntityName);
        String format = StrUtil.format(FileUtil.readUtf8String(BASE_FILE_PATH + "/utils/generator/template/controller.template"), map);
        FileUtil.writeString(format, BASE_FILE_PATH + "/controller/" + entityName + "Controller" + ".java", "UTF-8");
        System.out.println(entityName + "Controller生成成功！");
    }

    /**
     * 生成XML
     */
    private static void createXml(String entityName) {
        Map<String, Object> map = new HashMap<>(1);
        map.put("entityName", entityName);
        String format = StrUtil.format(FileUtil.readUtf8String(BASE_FILE_PATH + "/utils/generator/template/mapper_xml.template"), map);
        FileUtil.writeString(format, System.getProperty("user.dir") + "/src/main/resources/mapper/" + entityName + ".xml", "UTF-8");
        System.out.println(entityName + ".xml生成成功！");
    }

    /**
     * 生成页面
     */
    private static void createVueHtml(String entityName, String tableName) throws SQLException {
        String lowerEntityName = entityName.substring(0, 1).toLowerCase() + entityName.substring(1);
        Map<String, String> map = new HashMap<>(5);
        map.put("entityName", entityName);
        map.put("lowerEntityName", lowerEntityName);
        map.put("modelName", MODEL_NAME);
        List<TableColumn> tableColumns = getTableColumns(tableName);
        StringBuilder tableColumnBuilder = new StringBuilder();
        StringBuilder formItemBuilder = new StringBuilder();
        for (TableColumn tableColumn : tableColumns) {
            if ("id".equals(tableColumn.getColumnName())) {
                continue;
            }
            // 生成表格
            if (tableColumn.getColumnName().endsWith("file")) {
                tableColumnBuilder.append(SPACE8  + "<el-table-column label=\"文件\"><template slot-scope=\"scope\"><el-image style=\"width: 100px; height: 100px\" :src=\"scope.row.file\" :preview-src-list=\"[scope.row.file]\"></el-image></template></el-table-column>\n");
            } else if (tableColumn.getColumnName().endsWith("img")) {
                tableColumnBuilder.append(SPACE8  + "<el-table-column label=\"图片\"><template slot-scope=\"scope\"><el-image style=\"width: 100px; height: 100px\" :src=\"scope.row.img\" :preview-src-list=\"[scope.row.img]\"></el-image></template></el-table-column>\n");
            } else {
                tableColumnBuilder.append(SPACE8  + "<el-table-column prop=\"" + StrUtil.toCamelCase(tableColumn.getColumnName()) + "\" label=\"" + tableColumn.getColumnComment() + "\"></el-table-column>\n");
            }

            StringBuilder formBuilder = formItemBuilder.append(SPACE12  + "<el-form-item label=\"" + tableColumn.getColumnComment() + "\" label-width=\"150px\">\n");
            if (tableColumn.getColumnName().endsWith("time")) {
                // 日期时间
                formBuilder.append(SPACE12  + SPACE4 + "<el-date-picker style=\"width: 80%\" v-model=\"entity." + StrUtil.toCamelCase(tableColumn.getColumnName()) + "\" type=\"datetime\" value-format=\"yyyy-MM-dd HH:mm:ss\" placeholder=\"选择日期时间\"></el-date-picker>\n");
            } else if (tableColumn.getColumnName().endsWith("date")) {
                // 日期
                formBuilder.append(SPACE12  + SPACE4 + "<el-date-picker style=\"width: 80%\" v-model=\"entity." + StrUtil.toCamelCase(tableColumn.getColumnName()) + "\" type=\"date\" value-format=\"yyyy-MM-dd\" placeholder=\"选择日期\"></el-date-picker>\n");
            } else if (tableColumn.getColumnName().endsWith("_radio")) {
                // 单选
                String columnComment = tableColumn.getColumnComment();
                String[] split = columnComment.split(",");
                for (String s : split) {
                    formBuilder.append(SPACE12  + SPACE4 + "<el-radio v-model=\"entity." + StrUtil.toCamelCase(tableColumn.getColumnName()) + "\" label=\"" + s + "\">" + s + "</el-radio>\n");
                }
            } else if (tableColumn.getColumnName().endsWith("_rel")) {
                //下拉框，还需要自己写查询
                String[] s1 = tableColumn.getColumnName().split("_");
                String relTableName = s1[0];
                formBuilder.append(SPACE12  + SPACE4 + "<el-select v-model=\"entity." + StrUtil.toCamelCase(tableColumn.getColumnName()) + "\" placeholder=\"请选择\" style=\"width: 80%\">\n");
                formBuilder.append(SPACE12  + SPACE4 + SPACE4 + "<el-option v-for=\"item in options\" :key=\"item.id\" :label=\"item.name\" :value=\"item.name\"></el-option>\n");
                formBuilder.append(SPACE12  + SPACE4 + "</el-select>\n");
            } else if (tableColumn.getColumnName().endsWith("file") || tableColumn.getColumnName().endsWith("img")) {
                // 文件上传
                formBuilder.append(SPACE12  + SPACE4 + "<el-upload action=\"http://localhost:8888/files/upload\" :on-success=\"fileSuccessUpload\" :file-list=\"fileList\">\n");
                formBuilder.append(SPACE12  + SPACE4 + SPACE4 + "<el-button size=\"small\" type=\"primary\">点击上传</el-button>\n");
                formBuilder.append(SPACE12  + SPACE4 + "</el-upload>\n");
            } else {
                formBuilder.append(SPACE12  + SPACE4 + "<el-input v-model=\"entity." + StrUtil.toCamelCase(tableColumn.getColumnName()) + "\" autocomplete=\"off\" style=\"width: 80%\"></el-input>\n");
            }
            formBuilder.append(SPACE12  + "</el-form-item>\n");
        }
        map.put("tableColumn", tableColumnBuilder.toString());
        map.put("formItem", formItemBuilder.toString());
        if (IS_RESERVE) {
            String publishFormat = StrUtil.format(FileUtil.readUtf8String(BASE_FILE_PATH + "/utils/generator/template/publish.template"), map);
            FileUtil.writeString(publishFormat, System.getProperty("user.dir") + "/src/main/resources/static/page/end/publish.html", "UTF-8");

            String reserveFormat = StrUtil.format(FileUtil.readUtf8String(BASE_FILE_PATH + "/utils/generator/template/reserve.template"), map);
            FileUtil.writeString(reserveFormat, System.getProperty("user.dir") + "/src/main/resources/static/page/end/reserve.html", "UTF-8");
            return;
        }
        if (IS_SUBMIT) {
            // TODO
            return;
        }
        String format = StrUtil.format(FileUtil.readUtf8String(BASE_FILE_PATH + "/utils/generator/template/vue.template"), map);
        FileUtil.writeString(format, System.getProperty("user.dir") + "/src/main/resources/static/page/end/" + lowerEntityName + ".html", "UTF-8");
        System.out.println(lowerEntityName + ".html生成成功！");

        //生成菜单，预约/提交审核模型不用生成，手动在页面新增
        if (!IS_RESERVE && !IS_SUBMIT) {
            String delSql = "DELETE from t_permission where path = ?";
            Db.use(DATA_SOURCE).execute(delSql, lowerEntityName);
            String createSql = "INSERT INTO `t_permission` (`name`, `description`, `path`) VALUES ('" + MODEL_NAME + "管理', " +
                    "'" + MODEL_NAME + "管理', '" + lowerEntityName + "');";
            Db.use(DATA_SOURCE).execute(createSql);
            System.out.println(lowerEntityName + "菜单生成成功！");
        }
    }

    /**
     * 获取实体名称
     */
    private static String getEntityName() {
        return StrUtil.isBlank(MybatisCodeGenerator.TABLE[1]) ? toCamelFirstUpper(MybatisCodeGenerator.TABLE[0]) : MybatisCodeGenerator.TABLE[1];
    }

    /**
     * 转驼峰，第一个字母大写
     */
    private static String toCamelFirstUpper(String str) {
        String s = StrUtil.toCamelCase(str);
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }

    private static String convertDataType(String sqlType) {
        switch (sqlType) {
            case "varchar":
            case "longtext":
            case "text":
                return "String";
            case "double":
                return "Double";
            case "int":
                return "Integer";
            case "tinyint":
                return "Boolean";
            case "bigint":
                return "Long";
            case "datetime":
            case "timestamp":
                return "Date";
            case "decimal":
                return "BigDecimal";
            default:
                return "";
        }
    }
}
