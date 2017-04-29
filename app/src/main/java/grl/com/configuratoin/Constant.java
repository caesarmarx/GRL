package grl.com.configuratoin;

public class Constant {

	public static final String USER_ID = "user_id";
	public static final String USER_NAME = "user_name";
	public static final String USER_PHOTO = "user_photo";

	public static final String NEW_FRIENDS_USERNAME = "item_new_friends";
	public static final String GROUP_USERNAME = "item_groups";
	public static final String MESSAGE_ATTR_IS_VOICE_CALL = "is_voice_call";
	public static final String MESSAGE_ATTR_IS_VIDEO_CALL = "is_video_call";
	public static final String ACCOUNT_REMOVED = "account_removed";

	// constant key for intent data
	public static final String TITLE_ID = "TITLE_NAME";
	public static final String BACK_TITLE_ID = 	"back_title";
	public static final String PHOTO_PATH = "photo_path";

	// Order
	public static final String ORDER_ID = "order_id";
	public static final String CONTENT_ID = "content_id";

	public static final String ORDER_BUDGET = "budget";

	public static final String ORDER_GENDER = "gender";
	public static final String ORDER_AREA = "area";
	public static final String ORDER_NUMBER = "number";
	public static final String ORDER_MIN_DISTANCE = "min_distance";
	public static final String ORDER_MAX_DISTANCE = "max_distance";
	public static final String ORDER_LATITUDE = "latitude";
	public static final String ORDER_LONGITUDE = "longitude";
	public static final String ORDER_DIRECTION = "direction";

	public static final String ORDER_CONTENT_ID = "content_id";
	// new Energy
	public static final int ENERGY_ACCEPT = 0;
	public static final int ENERGY_ACCEPTED = 1;
	public static final int ENERGY_REQUIR = 2;
	public static final int ENERGY_REQUIRED = 3;

	// activity result key
	public static final String TEXTFIELD_CONTENT = "textfield_result_action";
	public static final String CONTENT_VIEW_KEY	= "content_view_action";
	public static final String TGROUP_ID = "tgroup_id";
	public static final String TGROUP_NAME = "tgroup_name";
	public static final String TGROUP_PHOTO = "tgroup_photo";
	public static final String DATA_KEY = "data_key";
	public static final String STATUS_TYPE_KEY = "status_type_key";
	public static final String FIHGT_INDEX_KEY = "fight_index-key";
	public static final String VIDEO_PATH = "video_path";

	// self profile setting constant
	public static final String EST_HELP = "你锦囊获评价、问道评价、师徒评价、授业评价共同生成你的评价";
	public static final String SUCCESS_HELP = "成交指你所帮助别人提供锦囊并被认可选定的令单数目";
	public static final String DONATE_HELP = "记录你所参加的师门挑战赛获胜所得奖励将会帮助多少需要帮助的人";
	public static final String ENERGY_HELP = "你可以通过个人气场指数的积累和师徒圈的人脉叠加获取你自身的能量值：能量值越大你的资源越大，社会地位越高，未来无限";
	public static final String PRICE_HELP = "即收徒费，分收徒收费和收徒付费两种模式；现价定义规则：第一个认可被点击之前的收徒费和咨询问道费都是0；第一个认可被点击开始就开始有收徒费，第一个认可后的三十天内为第一个月，采用即时收徒费即X/15，单位是金条；第二个月执行第一个月最后一次收徒价格；第三个月及其以后收徒价格：N月/(N-1)月的收徒数量比值再乘以(N-1)月的收徒价格；即时现价不可以低于用户自身所获贵人认证级别对应最低收徒价；走势";

	// teacher-disciple group: 스승제자계에서 관계문자렬
	public static final String REL_GRAND = "师爷";
	public static final String REL_FATHER = "师父";
	public static final String REL_FATHER_OLD = "师兄";
	public static final String REL_FATHER_YOUNG = "师弟";
	public static final String REL_SON = "徒弟";
	public static final String REL_GRAND_SON = "徒孙";

	// relationship flag
	public static final String REL_FLAG = "rel_flag";
	public static final String REL_CONNECTED = "connected";
	public static final String REL_DISCONNECTED = "disconnected";

	// Chat Type
	public static final String CHAT_CONSULT = "CONSULT_CHAT";
	public static final String CHAT_TGROUP = "TGROUP_CHAT";
	public static final String CHAT_ORDER = "ORDER_CHAT";
	public static final String CHAT_FGROUP = "FGROUP_CHAT";

	// File Type
	public static final String MSG_TEXT_TYPE = "MSG_TEXT";
	public static final String MSG_EMOTICON_TYPE = "MSG_EMOTICON";
	public static final String MSG_IMAGE_TYPE = "MSG_IMAGE";
	public static final String MSG_VOICE_TYPE = "MSG_VOICE";
	public static final String MSG_VIDEO_TYPE = "MSG_VIDEO";
	public static final String MSG_FILE_TYPE = "MSG_FILE";
	public static final String MSG_CALL_TYPE = "MSG_CALL";
	public static final String MSG_LOCATION_TYPE = "MSG_LOCATION";
	public static final String MSG_DELETE_TYPE = "MSG_DELETE";

	public static final String MSG_SEND_SUCCESS = "Sended";
	public static final String MSG_SEND_FAIL = "Send Failed";
	public static final String MSG_SEND_PROGRESS = "Sending";

	public static final String MSG_RECEIVE_SUCCESS = "Sended";
	public static final String MSG_RECEIVE_FAIL = "Send Failed";
	public static final String MSG_RECEIVE_PROGRESS = "Sending";

	public static final int TYPE_TEXT_LEFT = 0;
	public static final int TYPE_TEXT_RIGHT = 1;
	public static final int TYPE_IMAGE_LEFT = 2;
	public static final int TYPE_IMAGE_RIGHT = 3;

	// Challenge Date
	public static final int CHALLENGE__INIT = 0;
	public static final int CHALLENGE__VIEW = 1;
	public static final int CHALLENGE__ACCEPT = 2;
	public static final int CHALLENGE__DECLINE = 3;

	public static final String CHALLENGE_ID = "challenge_id";

	// TGROUP / FGROUP
	public static final String TEACHER_ID = "teacher_id";
	public static final String TEACHER_NAME = "teacher_name";

	public static final String BOSS_ID = "boss_id";
	public static final String BOSS_NAME = "boss_name";
	public static final String BOSS_PHOTO = "boss_photo";


	public static final String ACTIVITY_TITLE = "Title";
	public static final String RIGHT_BTN_TITLE = "Right_Btn_Title";

	public static final String SAVED_LATITUDE = "latitude";
	public static final String SAVED_LONGITUDE = "longitude";
	public static final String MAP_THUMB = "thumb";
	public static final String SAVED_ZOOM = "zoom";

	public static final String PHONE_CONTACT = "phone_number_arr";

	//  Star
	public static final int BIG_STAR_WIDTH = 100;
	public static final int BIG_STAR_HEIGHT = 100;
	public static final int NORMAL_STAR_WIDTH = 75;
	public static final int NORMAL_STAR_HEIGHT = 75;
	public static final int SMALL_STAR_WIDTH = 50;
	public static final int SMALL_STAR_HEIGHT = 50;

	//  Chat Image Width / Height
	public static final int BIG_IMAGE_WIDTH = 394;
	public static final int BIG_IMAGE_HEIGHT = 512;
	public static final int SMALL_IMAGE_WIDTH = 262;
	public static final int SMALL_IMAGE_HEIGHT = 340;

	public static int IMAGE_WIDTH;
	public static int IMAGE_HEIGHT;


	// disciple price
	public static final String DISCIPLE_PRICE_HELP = "即收徒费，分收徒收费和收徒付费两种模式；现价定义规则：第一个认可被点击之前的收徒费和咨询问道费都是0；第一个认可被点击开始就开始有收徒费，第一个认可后的三十天内为第一个月，采用即时收徒费即X/15，单位是金条；第二个月执行第一个月最后一次收徒价格；第三个月及其以后收徒价格：N月/(N-1)月的收徒数量比值再乘以(N-1)月的收徒价格；即时现价不可以低于用户自身所获贵人认证级别对应最低收徒价；走势";

	// Popularity
	public static final String TEACHER_FACTOR = "SY_FACTOR";
	public static final int GOOD_PRAISE = 3;
	public static final int NORMAL_PRAISE = 1;
	public static final int BAD_PRAISE = -3;

	// Update UI Message

	public static final String UPDATE_MAIN_UI = "UPDATE_MAIN";
	public static final String UPDATE_REAL_ORDER = "UPDATE_REAL_ORDER";
	public static final String UPDATE_CONSULT_CHAT = "UPDATE_CONSULT_CHAT";
	public static final String UPDATE_FGROUP_CHAT = "UPDATE_FGROUP_CHAT";
	public static final String UPDATE_TGROUP_CHAT = "UPDATE_TGROUP_CHAT";
	public static final String UPDATE_ORDER_CHAT = "UPDATE_ORDER_CHAT";

	public static final String CALL_VIDEO = "CALL_VIDEO";

	// Chat Message Id
	public static final String MSG_SQL_ID = "MSG_SQL_ID";

	// broadcast
	public static final String NEW_ENERGY = "NEW_ENENRGY";
	public static final String NEW_ENERGY_CONTACT = "NEW_ENERGY_CONTACT";

	public static final int HTTP_TIME_OUT = 15000;
}