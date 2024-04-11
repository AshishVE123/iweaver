package com.social.iweaver.api;

import com.google.gson.JsonObject;
import com.social.iweaver.apiresponse.ConnectUsResponse;
import com.social.iweaver.apiresponse.addcomment.AddCommentResponse;
import com.social.iweaver.apiresponse.CreatePostResponse;
import com.social.iweaver.apiresponse.EditProfileResponse;
import com.social.iweaver.apiresponse.ForgotPasswordResponse;
import com.social.iweaver.apiresponse.GetUserDetailsResponse;
import com.social.iweaver.apiresponse.HandleRequestResponse;
import com.social.iweaver.apiresponse.LikePosteponse;
import com.social.iweaver.apiresponse.LogoutResponse;
import com.social.iweaver.apiresponse.RegistrationResponse;
import com.social.iweaver.apiresponse.RejectRequestResponse;
import com.social.iweaver.apiresponse.ReportPostApiResponse;
import com.social.iweaver.apiresponse.SentFriendRequestResponse;
import com.social.iweaver.apiresponse.UnfriendApiResponse;
import com.social.iweaver.apiresponse.appversion.AppVersion;
import com.social.iweaver.apiresponse.friendlist.FriendListResponse;
import com.social.iweaver.apiresponse.friendrequest.FriendRequestResponse;
import com.social.iweaver.apiresponse.globalpost.GlobalPostResponse;
import com.social.iweaver.apiresponse.imagepost.ImagePostResponse;
import com.social.iweaver.apiresponse.loginresponse.LoginResponse;
import com.social.iweaver.apiresponse.notificationresponse.NotificationResponse;
import com.social.iweaver.apiresponse.postresponse.PostResponse;
import com.social.iweaver.apiresponse.profileimageupload.UploadProfileImage;
import com.social.iweaver.apiresponse.searchresponse.SearchResponse;
import com.social.iweaver.apiresponse.usercomment.CommentResponse;
import com.social.iweaver.apiresponse.userpost.ProfileFeedResponse;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface EndPointInterface {
    /**
     * @param body
     * @return
     */
    @POST("sign-in")
    Call<LoginResponse> login(@Body JsonObject body);

    /**
     * @param body
     * @return
     */
    @POST("sign-up")
    Call<RegistrationResponse> signUp(@Body JsonObject body);

    /**
     * @param body
     * @return
     */
    @POST("forgot-password")
    Call<ForgotPasswordResponse> forgotPassword(@Header("Authorization") String header, @Body JsonObject body);

    /**
     * @return
     */
    @POST("logout")
    Call<LogoutResponse> logoutUser(@Header("Authorization") String header);

    /**
     *
     * @param token
     * @param searchText
     * @return
     */
    @GET("search")
    Call<SearchResponse> searchUser(@Header("Authorization") String token, @Query("search") String searchText);

    /**
     *
     * @param token
     * @param object
     * @return
     */

    @PUT("update-user-detail")
    Call<EditProfileResponse> editProfileInfo(@Header("Authorization") String token, @Body JsonObject object);

    /**
     * @param token
     * @param userId
     * @return
     */
    @GET("get-user-detail/{UserID}")
    Call<GetUserDetailsResponse> getUserDetails(@Header("Authorization") String token, @Path("UserID") String userId);

    /**
     * @param token
     * @param userId
     * @return
     */
    @GET("user-detail/{userId}")
    Call<ProfileFeedResponse> getUserPostsDetails(@Header("Authorization") String token, @Path("userId") String userId,@Query("page") int page);

    /**
     * @param token
     * @param object
     * @return
     */
    @POST("create-post")
    Call<CreatePostResponse> createUserPost(@Header("Authorization") String token, @Query("userId") String userId, @Body JsonObject object);

    /**
     *
     * @param token
     * @param text
     * @return
     */
    @Multipart
    @POST("create-image-post")
    Call<ImagePostResponse> postImageFeed(@Header("Authorization") String token, @Part MultipartBody.Part[] imageFiles,@Part MultipartBody.Part[] videoFiles, @Part("content") RequestBody text);
    /* Call<ImagePostResponse> postImageFeed(@Header("Authorization") String token, @Part MultipartBody.Part files, @Part("content") RequestBody text);*/

    /**
     *
     * @param token
     * @param files
     * @return
     */
    @Multipart
    @POST("profileImage")
    Call<UploadProfileImage> userImageUpload(@Header("Authorization") String token, @Part MultipartBody.Part files);

    @GET("posts")
    Call<GlobalPostResponse> getGlobalDetails(@Header("Authorization") String token, @Query("page") int page);

    /**
     *
     * @param token
     * @param object
     * @return
     */
    @POST("sendFriendRequest")
    Call<SentFriendRequestResponse> sendReuest(@Header("Authorization") String token, @Body JsonObject object);

    /**
     * @param token
     * @param object
     * @return
     */
    @POST("likes")
    Call<LikePosteponse> likePost(@Header("Authorization") String token, @Body JsonObject object);


    /**
     * @param token
     * @param object
     * @return
     */
    @POST("handle-friend-request")
    Call<HandleRequestResponse> handleRequest(@Header("Authorization") String token, @Body JsonObject object);

    /**
     * @param token
     * @param object
     * @return
     */
    @POST("rejectFriendRequest")
    Call<RejectRequestResponse> rejectRequest(@Header("Authorization") String token, @Body JsonObject object);

    /**
     *
     * @param token
     * @return
     */
    @GET("friends")
    Call<FriendListResponse> getFriendList(@Header("Authorization") String token,@Query("page") int page);

    /**
     * @param token
     * @param postId
     * @return
     */
    @GET("comments/{postId}")
    Call<CommentResponse> getAllComment(@Header("Authorization") String token, @Path("postId") String postId, @Query("page") int page);

    /**
     * @param token
     * @param object
     * @return
     */
    @POST("unfriend")
    Call<UnfriendApiResponse> callUnFriend(@Header("Authorization") String token, @Body JsonObject object);

    /**
     * @param token
     * @param object
     * @return
     */
    @POST("comment")
    Call<AddCommentResponse> addComment(@Header("Authorization") String token, @Body JsonObject object);

    /**
     *
     * @param token
     * @return
     */
    @GET("friend-request")
    Call<FriendRequestResponse> getAllRequest(@Header("Authorization") String token,@Query("page") int page);

    /**
     *
     * @param token
     * @param object
     * @return
     */
    @POST("report-post")
    Call<ReportPostApiResponse> repostPostApi(@Header("Authorization") String token, @Body JsonObject object);

    /**
     *
     * @param token
     * @param object
     * @return
     */
    @POST("connect-to-us")
    Call<ConnectUsResponse> writeUs(@Header("Authorization") String token, @Body JsonObject object);

    /**
     *
     * @param token
     * @return
     */
    @GET("notifications")
    Call<NotificationResponse> getAllNotification(@Header("Authorization") String token);

    /**
     *
     * @param token
     * @param postId
     * @return
     */
    @GET("post-detail/{postId}")
    Call<PostResponse> getIndivitualPostDetails(@Header("Authorization") String token, @Path("postId") String postId);

    @POST("version-update")
    Call<AppVersion> postAppVersion(@Header("Authorization") String token,@Body JsonObject body);
}