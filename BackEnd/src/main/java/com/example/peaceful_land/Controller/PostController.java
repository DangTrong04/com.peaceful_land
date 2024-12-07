package com.example.peaceful_land.Controller;

import com.example.peaceful_land.DTO.*;
import com.example.peaceful_land.Entity.Post;
import com.example.peaceful_land.Entity.RequestPost;
import com.example.peaceful_land.Service.IPostService;
import com.example.peaceful_land.Utils.VariableUtils;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/posts")
public class PostController {

    private final IPostService postService;
    private final Gson gson;

    @PostMapping("/create-post")
    public ResponseEntity<?> createPost(@RequestBody PostRequest request) {
        Post newPost = postService.createPost(request);
        return ok(newPost);
    }

    @PostMapping(value = "/change-thumbnail", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> changeThumbnail(@ModelAttribute ChangePostThumbnailRequest request) {
        String result = postService.changeThumbnail(request);
        return ok(result);
    }

    @PostMapping("/request-approve")
    public ResponseEntity<?> requestApprove(@RequestBody IdRequest request) {
        RequestPost newRequest = postService.createUserPostRequestApproval(request);
        return ok(newRequest);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getPostInformation(@PathVariable Long id, @RequestBody IdRequest request) {
        request.setPostId(id);
        ViewPostResponse post = postService.getPostInformationFromPostId(request);
        return ok(post);
    }

    @PostMapping("/{id}/interest")
    public ResponseEntity<?> interestPost(@PathVariable Long id, @RequestBody InterestPostRequest request) {
        request.setPostId(id);
        return ResponseEntity.ok(gson.toJson(postService.interestPost(request)));
    }

    @PostMapping("/{id}/update-permission")
    public ResponseEntity<?> getUpdatePostPermission(@PathVariable Long id, @RequestBody IdRequest request) {
        request.setPostId(id);
        ResponsePostUpdatePermission permission = postService.getUpdatePostPermission(request);
        return ok(permission);
    }

    @PostMapping(value = "/{id}/update", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updatePost(@PathVariable Long id, @RequestParam String action, @ModelAttribute UpdatePropertyPostRequest request) {
        // Lấy thông tin bài rao
        Post updatePost = postService.checkPostExists(id);
        // Kiểm tra quyền cập nhật
        if (updatePost.getProperty().getUser().getId() != request.getUser_id()) {
            throw new DataIntegrityViolationException("Bạn không có quyền cập nhật bài rao này");
        }
        // Cập nhật thông tin hành động cần thực hiện
        request.setAction(VariableUtils.getUpdateActionString(action));
        // Thực hiện cập nhật
        switch (action) {
            case VariableUtils.UPDATE_TYPE_SOLD -> {
                // Xử lý trường hợp đã bán
                postService.updatePost_SoldOrRented(updatePost, request, true);
                return ok("Cập nhật trạng thái đã bán bất động sản thành công");
            }
            case VariableUtils.UPDATE_TYPE_RENTED -> {
                // Xử lý trường hợp đã cho thuê
                postService.updatePost_SoldOrRented(updatePost, request, false);
                return ok("Cập nhật trạng thái đã cho thuê bất động sản thành công");
            }
            case VariableUtils.UPDATE_TYPE_RESALE -> {
                // Xử lý trường hợp mua bán lại
                postService.updatePost_ReSaleOrReRent(updatePost, request, true);
                return ok("Cập nhật trạng thái mua bán lại bất động sản thành công");
            }
            case VariableUtils.UPDATE_TYPE_RERENT -> {
                // Xử lý trường hợp cho thuê lại
                postService.updatePost_ReSaleOrReRent(updatePost, request, false);
                return ok("Cập nhật trạng thái cho thuê lại bất động sản thành công");
            }
            case VariableUtils.UPDATE_TYPE_PRICE -> {
                // Xử lý trường hợp cập nhật giá
                postService.updatePost_Price(updatePost, request);
                return ok("Cập nhật giá bất động sản thành công");
            }
            case VariableUtils.UPDATE_TYPE_OFFER -> {
                // Xử lý trường hợp thay đổi hình thức
                postService.updatePost_Offer(updatePost, request);
                return ok("Cập nhật hình thức bài đăng thành công");
            }
            case VariableUtils.UPDATE_TYPE_RENTAL_PERIOD -> {
                // Xử lý trường hợp thay đổi hạn cho thuê
                postService.updatePost_RentalPeriod(updatePost, request);
                return ok("Cập nhật hạn cho thuê bài đăng thành công");
            }
            case VariableUtils.UPDATE_TYPE_DISCOUNT -> {
                // TODO: Xử lý trường hợp giảm giá
                postService.updatePost_Discount(updatePost, request);
                return ok("Cập nhật giảm gía bài đăng thành công");
            }
            default -> {
                // Xử lý trường hợp cập nhật thông tin bất động sản
                String result = postService.updatePost_Information(updatePost, request);
                return ok("Cập nhật thông tin bài rao bất động sản: " + result);
            }
        }
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchPost (
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestBody SearchPostRequest request ){
        return ok(postService.searchPost(request, page, size));
    }

    @GetMapping("/find-nearest-{number}")
    public ResponseEntity<?> searchNearestTopK (@PathVariable int number, @RequestBody NearestPostsRequest request) {
        request.setNumber(number);
        return ok(postService.findNearestPosts(request));
    }

    @GetMapping("/{id}/property-logs")
    public ResponseEntity<?> getPropertyUpdateHistory(@PathVariable Long id) {
        return ok(postService.getPropertyUpdateHistory(IdRequest.builder().postId(id).build()));
    }

    @GetMapping("/{id}/post-logs")
    public ResponseEntity<?> getPostUpdateHistory(@PathVariable Long id) {
        return ok(postService.getPostUpdateHistory(IdRequest.builder().postId(id).build()));
    }

}
