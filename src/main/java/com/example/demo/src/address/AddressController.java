package com.example.demo.src.address;

import com.example.demo.src.address.model.GetUserAddressRes;

import static com.example.demo.config.BaseResponseStatus.*;

import com.example.demo.src.address.model.PatchAddressReq;
import com.example.demo.src.address.model.PostaddressReq;
import com.example.demo.src.address.model.PostaddressRes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/app/address")
public class AddressController {

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private final AddressProvider addressProvider;
    @Autowired
    private final AddressService addressService;
    @Autowired
    private final JwtService jwtService;

    public AddressController(AddressProvider addressProvider, AddressService addressService, JwtService jwtService){
        this.addressProvider = addressProvider;
        this.addressService = addressService;
        this.jwtService = jwtService;
    }



    /**
     * 배송지 등록 API
     *
     * */
    @ResponseBody
    @PostMapping ("")
    public BaseResponse<PostaddressRes> postaddressInfo(@RequestBody PostaddressReq postaddressReq){
        try{
            int userIdxByJwt = jwtService.getUserIdx();
            //userIdx와 접근한 유저가 같은지 확인
            if(postaddressReq.getUserIdx() != userIdxByJwt){
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            PostaddressRes postaddressRes = addressService.postaddress(postaddressReq);

            return new BaseResponse<>(postaddressRes);
        }catch (BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 배송지 정보 수정 API
     * */
    @ResponseBody
    @PatchMapping ("{addressIdx}")
    public BaseResponse<String> patchaddressInfo(@RequestBody PatchAddressReq patchAddressReq, @PathVariable("addressIdx") int addressIdx){
        try{
            int userIdxByJwt = jwtService.getUserIdx();
            //userIdx와 접근한 유저가 같은지 확인
            if(patchAddressReq.getUserIdx() != userIdxByJwt){
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            addressService.patchaddressInfo(patchAddressReq,addressIdx);
            String result = "주소 변경 완료";
            return new BaseResponse<>(result);
        }catch (BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }


    /**
     * 배송지 삭제 API
     * */
    @ResponseBody
    @PatchMapping ("{addressIdx}/delete")
    public BaseResponse<String> deleteaddressInfo(@PathVariable("addressIdx") int addressIdx){
        try{
            int userIdx = jwtService.getUserIdx();
            //userIdx와 접근한 유저가 같은지 확인
            addressService.deleteaddressInfo(addressIdx,userIdx);
            String result = "주소가 삭제 되었습니다";
            return new BaseResponse<>(result);
        }catch (BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }


    /**
     * 유저가 등록한 주소지 조회
     * */
    @ResponseBody
    @GetMapping ("")
    public BaseResponse<List<GetUserAddressRes>> getaddressInfo() {
        try{
            int userIdx = jwtService.getUserIdx();
            //userIdx와 접근한 유저가 같은지 확인

            List<GetUserAddressRes> getUserAddressResList = addressProvider.getaddressInfo(userIdx);
            return new BaseResponse<>(getUserAddressResList);
        }catch (BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }


}
