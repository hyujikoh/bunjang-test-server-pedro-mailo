package com.example.demo.src.product;

import com.example.demo.src.product.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class ProductDao {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

/** 거래정보 생성 */
    public int createPayment(PostPaymentReq postPaymentReq, int buyerIdx, int productIdx){
        System.out.println("제품생성 dao 들어옴");
        String createPaymentQuery = "insert into Payment (productIdx, buyerIdx, safetyTax," +
                "point, totalPaymentAmount, paymentMethod, transactionMethod, address) VALUES (?,?,?,?,?,?,?,?)";
        int buyerIdxParm = buyerIdx;
        int productIdxParm = productIdx;
        System.out.println(buyerIdx);
        System.out.println(productIdx);
        this.jdbcTemplate.update(createPaymentQuery,productIdxParm, buyerIdxParm, postPaymentReq.getSafetyTax(),
                postPaymentReq.getPoint(), postPaymentReq.getTotalPaymentAmount(), postPaymentReq.getPaymentMethod(), postPaymentReq.getTransactionMethod(),
                postPaymentReq.getAddress());
        String lastInsertIdQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInsertIdQuery, int.class);
    }

    public int changeProductProgress(int productIdx) {
        String changeProgressQuery = "update Product P\n" +
                "set P.progress = 3\n" +
                "where P.Idx = ?";
        int productParams = productIdx;
        Object[] changeProgressParams = new Object[]{productIdx};
        return this.jdbcTemplate.update(changeProgressQuery, changeProgressParams);
    }





    /** 제품 생성 **/
    public int createProduct(PostProductReq postProductReq, int userIdx){
        System.out.println("제품생성 Dao 들어옴");
        String createProductQuery = "insert into Product (userIdx, categoryIdx, " +
                "productName, productDesc, " +
                "productCondition, saftyPay," +
                "isExchange, amount," +
                "includeFee, price, directtrans) VALUES (?,?,?,?,?,?,?,?,?,?,?);";
        int userIdxParams = userIdx;
        System.out.println(userIdxParams);

        System.out.println(createProductQuery);

        this.jdbcTemplate.update(createProductQuery, userIdxParams, postProductReq.getCategoryIdx(),
                postProductReq.getProductName(), postProductReq.getProductDesc(), postProductReq.getProductCondition(),
                postProductReq.getSaftyPay(), postProductReq.getIsExchange(), postProductReq.getAmount(),
                postProductReq.getIncludeFee(), postProductReq.getPrice(), postProductReq.getDirecttrans());
        System.out.println("확인1");
        String lastInserIdQuery = "select last_insert_id()";
        System.out.println("확인2");
        return this.jdbcTemplate.queryForObject(lastInserIdQuery, int.class);
    }












/** 제품 상세조회 **/
    public GetProductDetailRes getProductDetailRes(int userIdx, int productIdx){
        System.out.println("dao 들어옴");
        System.out.println(userIdx);
        System.out.println(productIdx);
        String getProductDetailQuery = "select P.Idx PIdx, group_concat(distinct PI.imageUrl) imageUrl,\n" +
                "       P.price, P.saftyPay,\n" +
                "       P.productName,\n" +
                "       case when 24 >= timestampdiff(HOUR, P.createAt, current_timestamp)\n" +
                "                           then concat(timestampdiff(HOUR, P.createAt, current_timestamp),'시간 전')\n" +
                "                           else concat(timestampdiff(DAY, P.createAt, current_timestamp), '일 전') end createAt,\n" +
                "       count(distinct V.Idx) viewCount, count(distinct F.Idx) likeCount,\n" +
                "       P.directtrans,\n" +
                "       P.productCondition, P.includeFee, P.amount,\n" +
                "       P.productDesc,\n" +
                "       group_concat(distinct PT.tagName) tag,\n" +
                "       C.categoryName,\n" +
                "       U.Idx UIdx, U.profileImage, U.shopName,\n" +
                "       count(distinct FW.Idx) follower,\n" +
                "       avg(distinct FORRATE.reviewRate) avgStar,\n" +
                "       case when UF.FavoriteUserIdx = ? then 1\n" +
                "            else 0 end myLike\n" +
                "from Product P\n" +
                "left join ProductImage PI on P.Idx = PI.productIdx\n" +
                "join Views V on P.Idx = V.productIdx\n" +
                "join Favorite F on P.Idx = F.productIdx\n" +
                "join ProductTag PT on P.Idx = PT.productIdx\n" +
                "join Category C on P.categoryIdx = C.Idx\n" +
                "join User U on P.userIdx = U.Idx\n" +
                "join Follow FW on U.Idx = FW.followingIdx\n" +
                "join ((select P2.Idx, P2.userIdx, PYR.productIdx, PYR.reviewRate\n" +
                "    from Product P2\n" +
                "    join (select PY.productIdx,R.reviewRate\n" +
                "                from Payment PY\n" +
                "                join Review R on PY.Idx = R.paymentIdx) as PYR on PYR.productIdx = P2.Idx) as FORRATE)\n" +
                "    on U.Idx = FORRATE.userIdx\n" +
                "left join (select U.Idx FavoriteUserIdx, F.productIdx FavoriteProcductIdx\n" +
                "      from Favorite F\n" +
                "      join User U on U.Idx = F.userIdx\n" +
                "      where U.Idx =?) UF on UF.FavoriteProcductIdx = P.Idx\n" +
                "\n" +
                "where P.Idx = ?\n" +
                "group by P.Idx";
        int GetUserIdx = userIdx;
        int GetProductIdx = productIdx;

        return  this.jdbcTemplate.queryForObject(getProductDetailQuery,
                (rs,rowNum) -> new GetProductDetailRes(
                        rs.getInt("PIdx"),
                        rs.getString("imageUrl"),
                        rs.getInt("price"),
                        rs.getInt("saftyPay"),
                        rs.getString("productName"),
                        rs.getInt("viewCount"),
                        rs.getInt("likeCount"),
                        rs.getString("directtrans"),
                        rs.getInt("productCondition"),
                        rs.getInt("includeFee"),
                        rs.getInt("amount"),
                        rs.getString("productDesc"),
                        rs.getString("tag"),
                        rs.getString("categoryName"),
                        rs.getInt("UIdx"),
                        rs.getString("profileImage"),
                        rs.getString("shopName"),
                        rs.getInt("follower"),
                        rs.getFloat("avgStar"),
                        rs.getInt("myLike")),
                GetUserIdx, GetUserIdx, GetProductIdx
                );
    }

    public int CreateView(int userIdx, int productIdx) {
        String createViewQuery = "insert into Views (userIdx,productIdx) values (?,?)";
        int userParams = userIdx;
        int productParams = productIdx;
        Object[] createViewParams = new Object[]{userIdx,productIdx};
        return this.jdbcTemplate.update(createViewQuery, createViewParams);
    }











/** 검색어로 제품 조회 **/
    public List<GetProductSearchRes> getProductSearchRes(String keyword){
        String getProductsQuery = "select P.Idx, P.price, P.productName, P.saftyPay, PI.imageUrl\n" +
                                  "from Product as P\n" +
                                  "left join ProductImage as PI on P.Idx = PI.productIdx\n" +
                                  "where P.productName like concat('%',?,'%')\n" +
                                  "group by P.Idx";
        String GetProductSearchResPrams = keyword;
        return this.jdbcTemplate.query(getProductsQuery,
                (rs,rowNum) -> new GetProductSearchRes(
                        rs.getInt("Idx"),
                        rs.getInt("price"),
                        rs.getString("productName"),
                        rs.getInt("saftyPay"),
                        rs.getString("imageUrl")),
                GetProductSearchResPrams);
    }   // getProductSearchRes() 끝



/** 카테고리로 제품 조회 **/
    public List<GetProductSearchRes> getProductByCategory(int categoryIdx){
        System.out.println("카테고리 dao 들어옴");
        String getProductsQuery = "select P.Idx, P.price, P.productName, P.saftyPay, PI.imageUrl\n" +
                "                from Product as P\n" +
                "                left join ProductImage as PI on P.Idx = PI.productIdx\n" +
                "                join Category as C on P.categoryIdx = C.Idx\n" +
                "                where C.Idx = ?\n" +
                "                group by P.Idx";
        int GetProductByCategoryPrams = categoryIdx;
        System.out.println("쿼리 파라미터 받음");
        return this.jdbcTemplate.query(getProductsQuery,
                (rs,rowNum) -> new GetProductSearchRes(
                        rs.getInt("Idx"),
                        rs.getInt("price"),
                        rs.getString("productName"),
                        rs.getInt("saftyPay"),
                        rs.getString("imageUrl")),
                GetProductByCategoryPrams);
    }   // getProductByCategory() 끝








} /** productDao class 닫는 괄호 **/






/**
private int Idx;    // 제품 인덱스
private int price;  // 제품 가격
private String productName; // 제품 제목
private int saftyPay;
 **/