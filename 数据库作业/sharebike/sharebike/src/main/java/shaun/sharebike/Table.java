package shaun.sharebike;

public class Table {
    //共享单车
    public static final String bikeroot="F:/bike.txt";
    //用户
    public static final String usersroot="F:/user.txt";
    //记录
    public static final String recordroot="F:/record.txt";

    //创建共享单车表
    public static final String bike="CREATE TABLE bike"+
            "(bike_id integer not null," +
            "primary key(bike_id))ENGINE=myisam DEFAULT CHARSET=utf8";
    
    //创建用户表
    public static final String users="create table users"+
            "(user_id integer  not null,"+
            "name varchar(255)  not null," +
            "phone_number varchar(15)  not null," +
            "balance float  not null," +
            "primary key(user_id))ENGINE=myisam DEFAULT CHARSET=utf8";
    
    //创建记录信息表
    public static final String record="create table record" +
            "(user_id integer not null," +
            "bike_id integer not null," +
            "start varchar(255) not null," +
            "start_time datetime not null," +
            "end varchar(255) not null," +
            "end_time datetime not null," +
            "primary key(user_id,bike_id,start_time))ENGINE=myisam DEFAULT CHARSET=utf8";


}
