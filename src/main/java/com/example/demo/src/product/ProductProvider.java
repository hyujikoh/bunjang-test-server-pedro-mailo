package com.example.demo.src.product;

import com.example.demo.config.BaseException;
//import com.example.demo.config.BaseResponseStatus;
import com.example.demo.src.product.model.*;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.*;

@Service
public class ProductProvider {
    private final ProductDao productDao;
    private final JwtService jwtService;

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public ProductProvider(ProductDao productDao, JwtService jwtService) {
        this.productDao = productDao;
        this.jwtService = jwtService;
    }

// 검색어로 제품 조회
    public List<GetProductSearchRes> getProductsBySearch(String keyword) throws BaseException{
        try{
            List<GetProductSearchRes> getProductSearchRes = productDao.getProductSearchRes(keyword);
            return  getProductSearchRes;
        }
        catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }

    }

// 카테고리로 제품 조회
    public List<GetProductSearchRes> getProductsByCategory(int categoryIdx) throws BaseException{
        System.out.println("카테고리 provider 들어옴");
        try{
            List<GetProductSearchRes> getProductByCategory = productDao.getProductByCategory(categoryIdx);
            System.out.println("getProductByCategory 나오나?");
            System.out.println(getProductByCategory);
            return  getProductByCategory;
        }
        catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }

    }






}  /** class ProductProvider 닫는괄호 **/
