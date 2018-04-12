package shaun.sharebike;

import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.Calendar;
import java.util.TimeZone;

public class BaseDao {
    Connection con=null;
    Statement statement=null;
    Statement statement1=null;
    //URL=协议名+IP地址(域名)+端口+数据库名称
    //由于使用的jdbc包是最高版本的，需要添加配置
    static final String url="jdbc:mysql://localhost:3306/sharebike?useUnicode=true&characterEncoding=UTF-8&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC&useSSL=true";
    static final String user="root";
    static final String password="123";
    //获取数据库连接，返回数据库连接对象
    public Connection getCon(){
         try {
             System.out.println("Connecting to a database...");
            //加载驱动程序
            Class.forName("com.mysql.jdbc.Driver");
            con= DriverManager.getConnection(url,user,password);
            statement=con.createStatement();
             System.out.println("Connected database successfully...");
        }catch(Exception e){
             System.out.println("Somrthing is wrong when connecting database ...");
            e.printStackTrace();
        }
        return  con;
    }
    //创建表
    public void creatTable(){
        try{
            long start=System.currentTimeMillis();
            System.out.println("Creating table in given database...");
            statement.executeUpdate("DROP TABLE IF EXISTS bike");
            statement.executeUpdate(Table.bike);
            statement.executeUpdate("DROP TABLE IF EXISTS users");
            statement.executeUpdate(Table.users);
            statement.executeUpdate("DROP TABLE IF EXISTS record");
            statement.executeUpdate(Table.record);
            System.out.println("Create table successfully...");
            long end=System.currentTimeMillis();
            System.out.println("建表的运行时间是："+(end-start)+"ms");
        }catch(Exception e){
            System.out.println("Something is wrong when creating table ...");
            e.printStackTrace();
        }
    }
    //向users表中插入数据
    public void insertToUsers(){
        File file=new File(Table.usersroot);
        String line=null;
        try{
            long start=System.currentTimeMillis();
            BufferedReader br=new BufferedReader(new InputStreamReader(new FileInputStream(file),"utf-8"));
            while((line=br.readLine())!=null){
                String[] user=line.split(";");
                String sql="insert into users(user_id,name,phone_number,balance)values("+user[0]+",'"+user[1]+"',"+user[2]+","+user[3]+")";
               // System.out.println(sql);
                statement.executeUpdate(sql);
            }
            long end=System.currentTimeMillis();
            System.out.println("向users表中插入数据的运行时间是："+(end-start)+"ms");
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    //向bike表中插入数据
    public void insertToBike(){
        File file=new File(Table.bikeroot);
        String line=null;
        try{
            long start=System.currentTimeMillis();
            BufferedReader br=new BufferedReader(new InputStreamReader(new FileInputStream(file),"gbk"));
            while((line=br.readLine())!=null){
                String sql="insert into bike(bike_id)values("+line+")";
                statement.executeUpdate(sql);
            }
            long end=System.currentTimeMillis();
            System.out.println("向bike表中插入数据的运行时间是："+(end-start)+"ms");
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    //向record表中插入数据
    public void insertToRecord(){
        File file=new File(Table.recordroot);
        String line=null;
        try{
            long start=System.currentTimeMillis();
            BufferedReader br=new BufferedReader(new InputStreamReader(new FileInputStream(file),"utf-8"));
            while((line=br.readLine())!=null){
                String[] user=line.split(";");
                String[] arr=user[3].split("[-:/]");
                user[3]="";
                for(String s:arr){
                    user[3]+=s;
                }
                String[] brr=user[5].split("[-:/]");
                user[5]="";
                for(String s:brr){
                user[5]+=s;
                }
                String sql="insert into record(user_id,bike_id,start,start_time,end,end_time)values("+user[0]+","+user[1]+",'"+user[2]+"',"+user[3]+",'"+user[4]+"',"+user[5]+")";
                statement.executeUpdate(sql);
            }
            long end=System.currentTimeMillis();
            System.out.println("向record表中插入数据的运行时间是："+(end-start)+"ms");
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    //往users表中添加地址
    public void  addAdress(){
        String sql="alter table users add address varchar(30)";
        String select="SELECT a.user_id,MAX(num),end FROM (SELECT user_id,count(*)as num,end FROM record WHERE DATE_FORMAT(end_time,'%h')>6 AND DATE_FORMAT(end_time,'%h')<12 GROUP BY user_id,end " +
                "ORDER BY user_id,num DESC )as a GROUP BY a.user_id";
        try{
            long start=System.currentTimeMillis();
            statement1=con.createStatement();
            ResultSet set=statement.executeQuery(select);
            statement1.executeUpdate(sql);
            while (set.next()){
               int user_id=set.getInt(1);
               String end=set.getString(3);
               String insert="update users set address='"+end+"' where user_id="+user_id;
               statement1.executeUpdate(insert);
           }
            long end=System.currentTimeMillis();
            System.out.println("向users表中添加地址字段的运行时间是："+(end-start)+"ms");
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    //计算费用
    public void calculate(){
        String cal="select user_id,bike_id,start_time,DATE_FORMAT(end_time,'%Y')-DATE_FORMAT(start_time,'%Y')as year,DATE_FORMAT(end_time,'%m')-DATE_FORMAT(start_time,'%m')as month," +
                " DATE_FORMAT(end_time,'%d')-DATE_FORMAT(start_time,'%d')as day,DATE_FORMAT(end_time,'%H')-DATE_FORMAT(start_time,'%H')as hour,DATE_FORMAT(end_time,'%i')-DATE_FORMAT(start_time,'%i')as minute" +
                " FROM record";
        String addfee="alter table record add fee int not null";
        int time=0,money=0;
        //获取开始时间
        long start=System.currentTimeMillis();
        try{
            statement1=con.createStatement();
            //添加字段fee
            statement.executeUpdate(addfee);
            ResultSet set=statement.executeQuery(cal);
            while (set.next()) {
                int user_id = set.getInt(1);
                int bike_id=set.getInt(2);
                String start_time=set.getString(3);
                int year = set.getInt(4);
                int month = set.getInt(5);
                int day = set.getInt(6);
                int hour = set.getInt(7);
                int minute = set.getInt(8);
                time = year * 365 * 30 * 24 * 60 + month * 30 * 24 * 60 + day * 24 * 60 + hour * 60 + minute;
                if(time<30){
                    money=1;
                }
                else if (time<60){
                    money=2;
                }
                else if(time<90){
                    money=3;
                }
                else{
                    money=4;
                }
                String feeToRecord="update record set fee="+money+" where user_id="+user_id+" and bike_id="+bike_id+" and start_time='"+start_time+"'";
                String feeToUsers="update users set balance=balance-"+money+" where user_id="+user_id;
                statement1.executeUpdate(feeToRecord);
                statement1.executeUpdate(feeToUsers);
            }

        long end=System.currentTimeMillis();
            System.out.println("添加fee字段的运行时间是："+(end-start)+"ms");

        }catch(Exception e){
            e.printStackTrace();
        }
    }
    //单车维修表
    public void repairBike()throws Exception {
        //创建视图bikedeatil用于记录每辆车使用的时间,最后使用的时间和地点
        String createView="CREATE VIEW bikedeatil AS" +
                "(select user_id,bike_id,end_time,end,DATE_FORMAT(end_time,'%m')as end_month,((DATE_FORMAT(end_time,'%Y')-DATE_FORMAT(start_time,'%Y'))*365*30*24*6+(DATE_FORMAT(end_time,'%m')-DATE_FORMAT(start_time,'%m'))*30*24*60+" +
                "     (DATE_FORMAT(end_time,'%d')-DATE_FORMAT(start_time,'%d'))*24*60+(DATE_FORMAT(end_time,'%H')-DATE_FORMAT(start_time,'%H'))*60+(DATE_FORMAT(end_time,'%i')-DATE_FORMAT(start_time,'%i')))as minute" +
                " FROM record)";
        statement.executeUpdate(createView);
        //获取月份
        Calendar c = Calendar.getInstance(TimeZone.getTimeZone("GMT+08:00"));
        //这里获取的月份就是上一个月
        int month = c.get(Calendar.MONTH) ;
        //由于给的数据中没有超过200小时的，所以建立的bikerepair是空的
        String sql="CREATE TABLE bikerepair(" +
                "SELECT a.bike_id,b.end FROM(SELECT bike_id ,SUM(minute)as min FROM bikedeatil GROUP BY bike_id)as a," +
                "(SELECT bike_id ,max(end_time)as max,end,end_month FROM bikedeatil GROUP BY bike_id)as b WHERE a.min/60>200 AND b.end_month="+month+") ";
        statement.executeUpdate(sql);


    }
    //关闭连接
    public void closeCon(){
        try{
            con.close();
            System.out.println("Goodbye!");
        }catch(SQLException e){
            e.printStackTrace();
        }

    }

}
