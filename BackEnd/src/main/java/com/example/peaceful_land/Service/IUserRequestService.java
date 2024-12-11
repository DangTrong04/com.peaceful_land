package com.example.peaceful_land.Service;

import com.example.peaceful_land.DTO.PostApprovalResponse;
import com.example.peaceful_land.DTO.ResponseUserPostReqView;
import com.example.peaceful_land.DTO.ResponseWithdrawRequest;

import java.util.List;

public interface IUserRequestService {

    List<PostApprovalResponse> getPostRequestBaseOn(String requestState);
    ResponseUserPostReqView getPostRequestById(Long id);
    void approvePostRequest(Long id);
    void rejectPostRequestFromId(Long id, String denyMessage);

    List<ResponseWithdrawRequest> getWithdrawRequestBaseOn(String requestState);
    void approveOrRejectWithdrawRequest(Long id, Boolean isApprove, String denyMessageIfFalse);

}