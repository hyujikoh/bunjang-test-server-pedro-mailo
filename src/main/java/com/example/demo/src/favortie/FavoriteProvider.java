package com.example.demo.src.favortie;


import com.example.demo.config.BaseException;
//import com.example.demo.config.BaseResponseStatus;

import com.example.demo.src.favortie.model.*;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.*;

@Service
public class FavoriteProvider {
    private final FavoriteDao favoriteDao;
    private final JwtService jwtService;

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public FavoriteProvider(FavoriteDao favoriteDao, JwtService jwtService) {
        this.favoriteDao = favoriteDao;
        this.jwtService = jwtService;
    }


    /**
     * 찜 정보 여부 조회
     * */
    public int checkFavoriteInfo(int userIdx,int productIdx) throws BaseException {
        try{
            return favoriteDao.checkFavoriteInfo(userIdx,productIdx);
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }

    }


    /**
     *
     * 유저가 찜한 상품 조회
     * */
    public List<GetUserFavoriteListRes> getFavoriteInfo(int userIdx) throws BaseException{

        try{
            List<GetUserFavoriteListRes> getUserFavoriteListRes = favoriteDao.getFavoriteInfo(userIdx);

            return  getUserFavoriteListRes;
        }
        catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }

    }
}
