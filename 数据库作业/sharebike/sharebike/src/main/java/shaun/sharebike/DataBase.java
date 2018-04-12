package shaun.sharebike;

public class DataBase {


    public static void main(String[] args) throws Exception{

        BaseDao baseDao=new BaseDao();
        baseDao.getCon();
        baseDao.creatTable();
        baseDao.insertToBike();
        baseDao.insertToUsers();
        baseDao.insertToRecord();
        baseDao.addAdress();
        baseDao.calculate();
        baseDao.repairBike();
        baseDao.closeCon();
    }

}
