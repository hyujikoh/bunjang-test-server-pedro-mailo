package com.example.demo.src.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.user.model.*;
import com.example.demo.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


import static com.example.demo.config.BaseResponseStatus.*;
import static com.example.demo.utils.ValidationRegex.isRegexEmail;

@RestController
@RequestMapping("/app/users")
public class UserController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private final UserProvider userProvider;
    @Autowired
    private final UserService userService;
    @Autowired
    private final JwtService jwtService;


    public UserController(UserProvider userProvider, UserService userService, JwtService jwtService){
        this.userProvider = userProvider;
        this.userService = userService;
        this.jwtService = jwtService;
    }


    /**첫번째 작업 3/21 00:41
     * 회원가입 API
     * [POST] /users
     * @return BaseResponse<PostUserRes>
     */
    // Body
    @ResponseBody
    @PostMapping("")
    public BaseResponse<PostUserRes> createUser(@RequestBody PostUserReq postUserReq) {


        // 핸드폰 번호, 유저이름, 생년월일을 공백으로 기재할시 나오는 BaseResponse<>(상태) 리턴
        if (postUserReq.getPhoneNumber() == null || postUserReq.getPhoneNumber().length() == 0) {
            return new BaseResponse<>(POST_USERS_EMPTY_PHONE);
        }
        if (postUserReq.getUserName() == null || postUserReq.getUserName().length() == 0) {
            return new BaseResponse<>(POST_USERS_EMPTY_NAME);
        }
        if (postUserReq.getUserBirth() == null || postUserReq.getUserBirth().length() == 0) {
            return new BaseResponse<>(POST_USERS_EMPTY_BIRTH);
        }
//        //이메일 정규표현 참고 코드
//        if(!isRegexEmail(postUserReq.getEmail())){
//            return new BaseResponse<>(POST_USERS_INVALID_EMAIL);
//        }
        try{
            PostUserRes postUserRes = userService.createUser(postUserReq);
            return new BaseResponse<>(postUserRes);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }
    /**
     * 로그인 API
     * [POST] /users/logIn
     * @return BaseResponse<PostLoginRes>
     */
    @ResponseBody
    @PostMapping("/logIn")
    public BaseResponse<PostLoginRes> logIn(@RequestBody PostLoginReq postLoginReq){
        try{
            // TODO: 로그인 값들에 대한 형식적인 validatin 처리해주셔야합니다!
            // TODO: 유저의 status ex) 비활성화된 유저, 탈퇴한 유저 등을 관리해주고 있다면 해당 부분에 대한 validation 처리도 해주셔야합니다.


            // 핸드폰 번호, 유저이름, 생년월일을 공백으로 기재할시 나오는 BaseResponse<>(상태) 리턴
            if (postLoginReq.getPhoneNumber() == null || postLoginReq.getPhoneNumber().length() == 0) {
                return new BaseResponse<>(POST_USERS_EMPTY_PHONE);
            }
            if (postLoginReq.getUserName() == null || postLoginReq.getUserName().length() == 0) {
                return new BaseResponse<>(POST_USERS_EMPTY_NAME);
            }
            if (postLoginReq.getUserBirth() == null || postLoginReq.getUserBirth().length() == 0) {
                return new BaseResponse<>(POST_USERS_EMPTY_BIRTH);
            }

            //입력한 핸드폰 번호가 DB에 되지 않은 핸드폰 번호 일경우
            if(userProvider.checkPhone(postLoginReq.getPhoneNumber())==0){
                //userProvider.logIn(postLoginReq);
                return new BaseResponse<>(POST_USERS_INVALID_PHONENUMBER);
            }

            PostLoginRes postLoginRes = userProvider.logIn(postLoginReq);
            return new BaseResponse<>(postLoginRes);
        } catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }




    /**
     * 유저 상점명 변경 API
     * [PATCH] /users/:userIdx
     * @return BaseResponse<String>
     */
    @ResponseBody
    @PatchMapping("/{userIdx}/shopName")
    public BaseResponse<String> modifyshopName(@PathVariable("userIdx") int userIdx, @RequestBody User user){
        try {
            //jwt에서 idx 추출.
            int userIdxByJwt = jwtService.getUserIdx();
            //userIdx와 접근한 유저가 같은지 확인
            if(userIdx != userIdxByJwt){
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            //같다면 유저네임 변경
            PatchShopNameReq patchShopNameReq = new PatchShopNameReq(userIdx,user.getShopName());
            userService.modifyshopName(patchShopNameReq);

            String result = "";
            return new BaseResponse<>(user.getShopName());
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }


    /**
     * 유저생년월일 변경 API
     * [PATCH] /users/:userIdx
     * @return BaseResponse<String>
     */
    @ResponseBody
    @PatchMapping("/{userIdx}/birth")
    public BaseResponse<String> modifyUserBirth(@PathVariable("userIdx") int userIdx, @RequestBody User user){
        try {
            //jwt에서 idx 추출.
            int userIdxByJwt = jwtService.getUserIdx();
            //userIdx와 접근한 유저가 같은지 확인
            if(userIdx != userIdxByJwt){
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            //같다면 유저 생년월일 변경
            PatchUserBirthReq patchUserBirthReq = new PatchUserBirthReq(userIdx,user.getUserBirth());
            userService.modifyUserBrith(patchUserBirthReq);
            return new BaseResponse<>(user.getUserBirth());
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 유저 성별변경 API
     * [PATCH] /users/:userIdx
     * @return BaseResponse<String>
     */
    @ResponseBody
    @PatchMapping("/{userIdx}/sex")
    public BaseResponse<String> modifyUserSex(@PathVariable("userIdx") int userIdx, @RequestBody PatchUserSexReq user){
        try {
            //jwt에서 idx 추출.
            int userIdxByJwt = jwtService.getUserIdx();
            //userIdx와 접근한 유저가 같은지 확인
            if(userIdx != userIdxByJwt){
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            //같다면 유저 생년월일 변경
            PatchUserSexReq patchUserSexReq = new PatchUserSexReq(userIdx,user.getUserSex());
            userService.modifyUserSex(patchUserSexReq);
            return new BaseResponse<>(user.getUserSex());
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }


    /**
     * 유저 핸드폰 번호변경 API
     * [PATCH] /users/:userIdx
     * @return BaseResponse<String>
     */
    @ResponseBody
    @PatchMapping("/{userIdx}/phonenumber")
    public BaseResponse<String> modifyPhoneNumber(@PathVariable("userIdx") int userIdx, @RequestBody User user){
        try {
            //jwt에서 idx 추출.
            int userIdxByJwt = jwtService.getUserIdx();
            //userIdx와 접근한 유저가 같은지 확인
            if(userIdx != userIdxByJwt){
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            //같다면 유저 생년월일 변경
            PatchUserPhoneReq patchUserPhoneReq = new PatchUserPhoneReq(userIdx,user.getPhoneNumber());
            userService.modifyUserPhone(patchUserPhoneReq);
            return new BaseResponse<>(user.getPhoneNumber());
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }





    /**
     * 유저 메인페이지 조회 API
     * [PATCH] /:userIdx/:progress
     * @return BaseResponse<list<String>>
     * 유저인덱스와 판매진행 상태 progress 를 매개변수로 받아서 유저 메인페이지 조회
     */
    @ResponseBody
    @GetMapping("/{userIdx}/{progress}") // (GET) 127.0.0.1:9000/app/hotels/:hotelIdx
    public BaseResponse<List<String>> getUserMainPage(@PathVariable("userIdx") int userIdx,@PathVariable("progress") int progress){
        try {
            //jwt에서 idx 추출.
            System.out.println(userIdx);
            int userIdxByJwt = jwtService.getUserIdx();
            System.out.println(userIdxByJwt);
            //userIdx와 접근한 유저가 같은지 확인
            if(userIdx != userIdxByJwt){
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            //같다면 유저네임 변경
            List<String> getUserMainPage = userProvider.getUserMainPage(userIdx,progress);

            String result = "";
            return new BaseResponse<>(getUserMainPage);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }

    }

    /**
     * 상점명 검색 API
     * [PATCH] /shopname?{shopName}
     * @return BaseResponse<list<GetSearchByUserNameRes>>
     *
     */
    @ResponseBody
    @GetMapping("/shopName")
    public BaseResponse<List<GetSearchByUserNameRes>> SeatchByUserName(@RequestParam(required = false) String shopName) { //
        try{

            // Get Users

            List<GetSearchByUserNameRes> getSearchByUserNameResList = userProvider.SearchByUserName(shopName); // 검색조회
            System.out.println("2");
            return new BaseResponse<>(getSearchByUserNameResList);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }


    /**
     * 유저 탈퇴 API
     * [PATCH] /users/:userIdx/delete
     *
     * @return BaseResponse<String>
     */
    @PatchMapping("/delete/{userIdx}")
    public BaseResponse<String> deleteUserInfo(@PathVariable("userIdx") int userIdx, @RequestBody DeleteUserReq deleteUserReq) {
        if (deleteUserReq.getClosingReason() == null) {
            return new BaseResponse<>(EMPTY_CLOSING_ACCOUNT_REASON);
        }
        try {
            //jwt에서 idx 추출.
            int userIdxByJwt = jwtService.getUserIdx();
            //userIdx와 접근한 유저가 같은지 확인
            if (userIdx != userIdxByJwt) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            //같다면 탈퇴로 status 변경
            deleteUserReq = new DeleteUserReq(userIdx
                    , deleteUserReq.getClosingReason());
            userService.deleteUserInfo(deleteUserReq);

            return new BaseResponse<>(SUCCESS_DELETE_USER);

        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }


    /**
    * 유저 상점 설정 정보 조회 API
    * */
    @ResponseBody
    @GetMapping("/{userIdx}/setting")
    public BaseResponse<List<GerUserSettingRes>> GetsettingInfo(@PathVariable("userIdx") int userIdx) { //
        try {
            //jwt에서 idx 추출.
            int userIdxByJwt = jwtService.getUserIdx();
            //userIdx와 접근한 유저가 같은지 확인
            if(userIdx != userIdxByJwt){
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            //같다면 유저네임 변경
            List<GerUserSettingRes> GetsettingInfoRes = userProvider.GetsettingInfo(userIdx);

            return new BaseResponse<>(GetsettingInfoRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }


    /**
     *  유저 상점 정보 설정 API
     */
    @ResponseBody
    @PatchMapping("/{userIdx}/setting")
    public BaseResponse<String> patchUserSetting(@RequestBody PatchUserSettingReq patchUserSettingReq,@PathVariable("userIdx") int userIdx) {
        try{
            int userIdxByJwt = jwtService.getUserIdx();
            //userIdx와 접근한 유저가 같은지 확인
            if(userIdx != userIdxByJwt){
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            userService.patchUserSetting(patchUserSettingReq,userIdx);
            String result = "설정변경완료";
            return new BaseResponse<>(result);
        }catch (BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }



}
