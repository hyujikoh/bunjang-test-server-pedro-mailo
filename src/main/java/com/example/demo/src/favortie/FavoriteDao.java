package com.example.demo.src.favortie;

import com.example.demo.src.favortie.model.*;
import com.example.demo.src.favortie.model.PostFavoriteInfoRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class FavoriteDao {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public int checkFavoriteInfo(int userIdx, int productIdx) {
        String checkShopNameQuery = "select exists(select userIdx from Favorite where userIdx = ? and productIdx = ?)";
        int checkUserIdxParams = userIdx;
        int checkProductIdxParams = productIdx;
        return this.jdbcTemplate.queryForObject(checkShopNameQuery,
                int.class,
                checkUserIdxParams,checkProductIdxParams);
    }

    public int createFavorite(PostFavoriteInfoReq postFavoriteInfoReq) {
        String deleteFavoriteQuery = "insert into Favorite (userIdx,productIdx) values (?,?)";
        int userParams = postFavoriteInfoReq.getUserIdx();
        int productParams = postFavoriteInfoReq.getProductIdx();
        Object[] deleteFavoriteParams = new Object[]{userParams,productParams};
        return this.jdbcTemplate.update(deleteFavoriteQuery, deleteFavoriteParams);

    }
    public int deleteFavorite(PostFavoriteInfoReq postFavoriteInfoReq) {
        String deleteFavoriteQuery = "update Favorite set status = 2 where userIdx = ? and productIdx =?";
        int userParams = postFavoriteInfoReq.getUserIdx();
        int productParams = postFavoriteInfoReq.getProductIdx();
        Object[] deleteFavoriteParams = new Object[]{userParams,productParams};
        return this.jdbcTemplate.update(deleteFavoriteQuery, deleteFavoriteParams);

    }
    public int statusChangeFavorite(PostFavoriteInfoReq postFavoriteInfoReq) {
        String statusChangeFavoriteQuery = "update Favorite set status = 1 where userIdx = ? and productIdx =?";
        int userParams = postFavoriteInfoReq.getUserIdx();
        int productParams = postFavoriteInfoReq.getProductIdx();
        Object[] statusChangeFavoriteParams = new Object[]{userParams,productParams};
        return this.jdbcTemplate.update(statusChangeFavoriteQuery, statusChangeFavoriteParams);

    }

    public PostFavoriteInfoRes getfav(PostFavoriteInfoReq postFavoriteInfoReq) {

        String gefavQuery = "select userIdx,productIdx,status from Favorite where userIdx = ? and productIdx =?";
        int userParams = postFavoriteInfoReq.getUserIdx();
        int productParams = postFavoriteInfoReq.getProductIdx();
        return this.jdbcTemplate.queryForObject(gefavQuery,
                (rs, rowNum) -> new PostFavoriteInfoRes(
                        rs.getInt("userIdx"),
                        rs.getInt("productIdx"),
                        rs.getInt("status")),
                userParams,productParams);
    }

    public List<GetUserFavoriteListRes> getFavoriteInfo(int userIdx) {

        String GetUserFavoriteListQuery ="select P.shopName,\n" +
                "       P.Idx,\n" +
                "       P.ProductName,\n" +
                "       P.ProductImage,\n" +
                "       P.price,\n" +
                "       Posteddate,\n" +
                "       P.saftyPay safePay,\n" +
                "       1 as myLike\n" +
                "from Favorite F\n" +
                "         left join (select P.Idx,\n" +
                "                           P.productName                                                                 ProductName,\n" +
                "                           P.price                                                                       ProductPrice,\n" +
                "                           P.saftyPay                                                                    SaftyPay,\n" +
                "                           PI.imageUrl                                                                   ProductImage,\n" +
                "                           P.userIdx,\n" +
                "                           P.progress,\n" +
                "                           P.price,\n" +
                "                           U.shopName,\n" +
                "                           case\n" +
                "                               when 24 >= timestampdiff(HOUR, P.updateAt, current_timestamp)\n" +
                "                                   then concat(timestampdiff(HOUR, P.updateAt, current_timestamp), '시간 전')\n" +
                "                               else concat(timestampdiff(DAY, P.updateAt, current_timestamp), '일 전') end Posteddate\n" +
                "                    from Product P\n" +
                "                             left join(select * from ProductImage group by productIdx) PI on P.Idx = PI.productIdx\n" +
                "                        and P.status = 1\n" +
                "                             left join User U on P.userIdx = U.Idx) P on P.Idx = F.productIdx\n" +
                "where F.userIdx = ?;";
        int GetUserIdx = userIdx;

        return  this.jdbcTemplate.query(GetUserFavoriteListQuery,
                (rs,rowNum) -> new GetUserFavoriteListRes(
                        rs.getString("shopName"),
                        rs.getInt("Idx"),
                        rs.getString("ProductName"),
                        rs.getString("ProductImage"),
                        rs.getInt("price"),
                        rs.getString("Posteddate"),
                        rs.getInt("safePay"),
                        rs.getInt("mylike")
                ),
                GetUserIdx
        );


    }
}

