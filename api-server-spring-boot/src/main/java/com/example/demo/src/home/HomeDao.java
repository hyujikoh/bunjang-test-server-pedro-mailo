package com.example.demo.src.home;

import com.example.demo.src.home.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class HomeDao {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }


// 메인페이지 광고리스트 조회
    public List<GetHomeAdRes> getAdListByHome( ){
        String getAdByHomeQuery = "select AD.Idx, " +
                                "AD.adImageUrl, " +
                                "AD.adTittle, " +
                                "AD.adDesc, " +
                                "AD.adLink\n" +
                                "from AD";

        return this.jdbcTemplate.query(getAdByHomeQuery,
                (rs, rowNum) -> new GetHomeAdRes(
                        rs.getInt("Idx"),
                        rs.getString("adImageUrl"),
                        rs.getString("adTittle"),
                        rs.getString("adDesc"),
                        rs.getString("adLink")
                )
        );

    }



// 메인페이지 상품조회
    public List<GetHomeProductRes> getProductListByHome(int userIdx){
        String getProductByHomeQuery = "select P.Idx,\n" +
                "       case when UF.FavoriteUserIdx = ? then 1\n" +
                "            else 0 end myLike,\n" +
                "       PI.imageUrl,\n" +
                "       P.price,\n" +
                "       P.saftyPay, P.productName,\n" +
                "       P.directtrans, case when 24 >= timestampdiff(HOUR, P.updateAt, current_timestamp)\n" +
                "                           then concat(timestampdiff(HOUR, P.updateAt, current_timestamp),'시간 전')\n" +
                "                           else concat(timestampdiff(DAY, P.updateAt, current_timestamp), '일 전') end createAt,\n" +
                "       count(distinct F2.Idx) productLike\n" +
                "from Product P\n" +
                "left join (select U.Idx FavoriteUserIdx, F.productIdx FavoriteProcductIdx\n" +
                "      from Favorite F\n" +
                "      join User U on U.Idx = F.userIdx\n" +
                "      where U.Idx =?) UF on UF.FavoriteProcductIdx = P.Idx\n" +
                "left join ProductImage PI on P.Idx = PI.productIdx\n" +
                "left join Favorite F2 on P.Idx = F2.productIdx\n" +
                "group by P.Idx";
        int GetProductByHomeParams = userIdx;
        return this.jdbcTemplate.query(getProductByHomeQuery,
                (rs, rowNum) -> new GetHomeProductRes(
                        rs.getInt("Idx"),
                        rs.getInt("myLike"),
                        rs.getString("imageUrl"),
                        rs.getInt("price"),
                        rs.getInt("saftyPay"),
                        rs.getString("productName"),
                        rs.getString("directtrans"),
                        rs.getString("creareAt"),
                        rs.getInt("productLike")
                ),
                GetProductByHomeParams, GetProductByHomeParams
                );
    }








}
