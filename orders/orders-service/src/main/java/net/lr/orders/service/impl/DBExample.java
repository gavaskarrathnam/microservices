package net.lr.orders.service.impl;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

public class DBExample {
    
    private DataSource dataSource;
    
    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }
    
    public void test() throws Exception {
    	
    	System.out.println("******** Calling from DBExample ***********");
    	
        StringBuffer queryBuf = new StringBuffer();
        queryBuf.append("select o.id,o.date,p.id, p.prodname,p.proddesc,p.prodtype,c.id,c.custfname, c.custlname,c.phone,c.email from orders o "); 
        queryBuf.append("left join products p on o.productid = p.id ");
        queryBuf.append("left join customers c on o.customerid = c.id");
        
        try {
			Connection con = dataSource.getConnection();			
            Statement stmt = con.createStatement();
			
            writeInfos(con);
            
            //TODO
            //dropTable(con);
            //stmt.execute("create table person (name varchar(100), twittername varchar(100))");
            //stmt.execute("insert into person (name, twittername) values ('Christian Schneider', '@schneider_chris')");
            //ResultSet rs = stmt.executeQuery("SELECT * FROM ORDERS");
            
            ResultSet rs = stmt.executeQuery(queryBuf.toString());
            //ResultSetMetaData meta = rs.getMetaData();
            
            System.out.println(" ***** Database details ******" );
            while (rs.next()) {
                //writeResult(rs, meta.getColumnCount());
            	
            	System.out.print("Order ID :" + rs.getString("o.id") + ", ");
            	System.out.print("Order Date :" + rs.getDate("o.date") + ", ");
				System.out.print("Cust Id :" + rs.getInt("c.id") + ", ");
				System.out.print("Cust phone :" + rs.getBigDecimal("c.phone"));
				System.out.println(" ");
            }
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            throw e;
        }

    }

    private void writeInfos(Connection con) throws SQLException {
        DatabaseMetaData dbMeta = con.getMetaData();
        System.out.println("From DBExample Using datasource " + dbMeta.getDatabaseProductName() + ", URL " + dbMeta.getURL());
    }


   /* private void dropTable(Connection con) {
        try{
			Statement stmt = con.createStatement();
            stmt.execute("drop table person");
        }
        catch (Exception e) {
            // Ignore as it will fail the first time
        }
    }*/

    /*private void writeResult(ResultSet rs, int columnCount) throws SQLException {
    	System.out.println("META DATA COLUMN count :" + columnCount );
        for (int c = 1; c <= columnCount; c++) {
            System.out.print("" + rs.getString(c) + ", ");
        }
        System.out.println();
    }*/

}	