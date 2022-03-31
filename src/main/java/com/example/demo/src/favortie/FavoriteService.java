package com.example.demo.src.favortie;

import com.example.demo.config.BaseException;
import com.example.demo.src.favortie.model.*;

import com.example.demo.utils.JwtService;
import com.example.demo.utils.SHA256;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.*;

@Service
public class FavoriteService {

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final FavoriteDao favoriteDao;
    private final FavoriteProvider favoriteProvider;
    private final JwtService jwtService;

    @Autowired
    public FavoriteService(FavoriteDao favoriteDao, FavoriteProvider favoriteProvider, JwtService jwtService) {
        this.favoriteDao = favoriteDao;
        this.favoriteProvider = favoriteProvider;
        this.jwtService = jwtService;

    }


    /**
     * 찜 생성 하기전에 기존에 등록한 찜 인지 , 또 존재한다면, 상태값에 따라 삭제, 등록여부 판단
     * */
    public PostFavoriteInfoRes PostFavoriteInfo(PostFavoriteInfoReq postFavoriteInfoReq) throws BaseException {

        if (favoriteProvider.checkFavoriteInfo(postFavoriteInfoReq.getUserIdx(),postFavoriteInfoReq.getProductIdx()) == 1) {
            PostFavoriteInfoRes getfav = favoriteDao.getfav(postFavoriteInfoReq);

            if(getfav.getStatus() ==1){
                favoriteDao.deleteFavorite(postFavoriteInfoReq);
            }
            if(getfav.getStatus() ==2){
                favoriteDao.statusChangeFavorite(postFavoriteInfoReq);
            }

        }
        else {
            favoriteDao.createFavorite(postFavoriteInfoReq);
        }
        PostFavoriteInfoRes getfav = favoriteDao.getfav(postFavoriteInfoReq);
        return new PostFavoriteInfoRes(getfav.getProductIdx(),getfav.getUserIdx(),getfav.getStatus());
    }


}
