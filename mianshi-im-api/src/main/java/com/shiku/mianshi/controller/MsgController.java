package com.shiku.mianshi.controller;

import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cn.xyz.commons.constants.KConstants.MsgType;
import cn.xyz.commons.constants.KConstants.Result;
import cn.xyz.commons.utils.ReqUtil;
import cn.xyz.commons.utils.StringUtil;
import cn.xyz.commons.vo.JSONMessage;
import cn.xyz.mianshi.example.AddCommentParam;
import cn.xyz.mianshi.example.AddGiftParam;
import cn.xyz.mianshi.example.AddMsgParam;
import cn.xyz.mianshi.example.MessageExample;
import cn.xyz.repository.MsgCommentRepository;
import cn.xyz.repository.MsgGiftRepository;
import cn.xyz.repository.MsgListRepository;
import cn.xyz.repository.MsgPraiseRepository;
import cn.xyz.repository.MsgRepository;

import com.alibaba.fastjson.JSON;

/**
 * 商务圈接口
 * 
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/b/circle/msg")
public class MsgController extends AbstractController {

	private static Logger logger = LoggerFactory.getLogger(MsgController.class);

	@Autowired
	private MsgCommentRepository commentRepository;
	@Autowired
	private MsgGiftRepository giftRepository;
	@Autowired
	private MsgRepository msgRepository;
	@Autowired
	private MsgPraiseRepository praiseRepository;

	@Autowired
	private MsgListRepository msgListRepository;

	@RequestMapping(value = "/hot")
	public JSONMessage getHostMsgList(@RequestParam(defaultValue = "0") int cityId,
			@RequestParam(defaultValue = "0") Integer pageIndex) {
		Object data = msgListRepository.getHotList(cityId, pageIndex, 0);
		return JSONMessage.success(null, data);
	}

	@RequestMapping(value = "/latest")
	public JSONMessage getLatestMsgList(@RequestParam(defaultValue = "0") int cityId,
			@RequestParam(defaultValue = "0") Integer pageIndex) {
		Object data = msgListRepository.getLatestList(cityId, pageIndex, 0);
		return JSONMessage.success(null, data);
	}
	//评论
	@RequestMapping(value = "/comment/add")
	public JSONMessage addComment(@ModelAttribute AddCommentParam param) {
		JSONMessage jMessage;

		if (StringUtil.isEmpty(param.getMessageId())) {
			jMessage = Result.ParamsAuthFail;
		} else {
			try {
				ObjectId data = commentRepository.add(ReqUtil.getUserId(), param);
				jMessage = null == data ? JSONMessage.failure(null) : JSONMessage.success(null, data);
			} catch (Exception e) {
				logger.error("评论失败", e);
				jMessage = JSONMessage.error(e);
			}
		}

		return jMessage;
	}

	@RequestMapping(value = "/gift/add")
	public JSONMessage addGift(@RequestParam String messageId, @RequestParam String gifts) {
		JSONMessage jMessage;

		if (StringUtil.isEmpty(messageId) || StringUtil.isEmpty(gifts)) {
			jMessage = Result.ParamsAuthFail;
		} else {
			try {
				Object data = giftRepository.add(ReqUtil.getUserId(), new ObjectId(messageId),
						JSON.parseArray(gifts, AddGiftParam.class));
				jMessage = null == data ? JSONMessage.failure(null) : JSONMessage.success(null, data);
			} catch (Exception e) {
				logger.error("送礼物失败", e);
				jMessage = JSONMessage.error(e);
			}
		}

		return jMessage;
	}

	@RequestMapping(value = "/add")
	public JSONMessage addMsg(@ModelAttribute AddMsgParam param) {
		JSONMessage jMessage = Result.ParamsAuthFail;

		if (0 == param.getType() || 0 == param.getFlag() || 0 == param.getVisible()) {
		} else if (MsgType.Text == param.getType() && StringUtil.isEmpty(param.getText())) {
		} else if (MsgType.Image == param.getType() && StringUtil.isEmpty(param.getImages())) {
		} else if (MsgType.Audio == param.getType() && StringUtil.isEmpty(param.getAudios())) {
		} else if (MsgType.Video == param.getType() && StringUtil.isEmpty(param.getVideos())) {
		} else {
			try {
				ObjectId data = msgRepository.add(ReqUtil.getUserId(), param);

				jMessage = JSONMessage.success(null, data);
			} catch (Exception e) {
				logger.error("发商务圈消息失败", e);

				jMessage = JSONMessage.error(e);
			}
		}

		return jMessage;
	}
	
	@RequestMapping(value = "/praise/add")
	public JSONMessage addPraise(@RequestParam String messageId) {
		JSONMessage jMessage;

		if (StringUtil.isEmpty(messageId)) {
			jMessage = Result.ParamsAuthFail;
		} else {
			try {
				ObjectId data = praiseRepository.add(ReqUtil.getUserId(), new ObjectId(messageId));
				jMessage = null == data ? JSONMessage.failure(null) : JSONMessage.success(null, data);
			} catch (Exception e) {
				logger.error("赞失败", e);

				jMessage = JSONMessage.error(e);
			}
		}

		return jMessage;
	}

	@RequestMapping(value = "/comment/delete")
	public JSONMessage deleteComment(@RequestParam String messageId, String commentId) {
		JSONMessage jMessage;

		if (StringUtil.isEmpty(messageId) || StringUtil.isEmpty(commentId)) {
			jMessage = Result.ParamsAuthFail;
		} else {
			try {
				boolean ok = commentRepository.delete(ReqUtil.getUserId(), new ObjectId(messageId),
						new ObjectId(commentId));
				jMessage = ok ? JSONMessage.success(null) : JSONMessage.failure(null);
			} catch (Exception e) {
				logger.error("删除评论失败", e);
				jMessage = JSONMessage.error(e);
			}
		}

		return jMessage;
	}

	@RequestMapping(value = "/delete")
	public JSONMessage deleteMsg(@RequestParam String messageId) {
		JSONMessage jMessage;

		if (StringUtil.isEmpty(messageId)) {
			jMessage = Result.ParamsAuthFail;
		} else {
			try {
				boolean ok = msgRepository.delete(ReqUtil.getUserId(), new ObjectId(messageId));
				jMessage = ok ? JSONMessage.success(null) : JSONMessage.failure(null);
			} catch (Exception e) {
				logger.error("删除商务圈消息失败", e);

				jMessage = JSONMessage.error(e);
			}
		}

		return jMessage;
	}

	@RequestMapping(value = "/praise/delete")
	public JSONMessage deletePraise(@RequestParam String messageId) {
		JSONMessage jMessage;

		if (StringUtil.isEmpty(messageId)) {
			jMessage = Result.ParamsAuthFail;
		} else {
			boolean ok = praiseRepository.delete(ReqUtil.getUserId(), new ObjectId(messageId));

			jMessage = ok ? JSONMessage.success(null) : JSONMessage.failure(null);
		}

		return jMessage;
	}

	@RequestMapping(value = "/forwarding")
	public JSONMessage forwardingMsg(@ModelAttribute AddMsgParam param) {
		JSONMessage jMessage;

		if (StringUtil.isEmpty(param.getText()) || StringUtil.isEmpty(param.getMessageId())) {
			jMessage = Result.ParamsAuthFail;
		} else {
			try {
				Object data = msgRepository.forwarding(ReqUtil.getUserId(), param);
				jMessage = null == data ? JSONMessage.failure(null) : JSONMessage.success(null, data);
			} catch (Exception e) {
				logger.error("转发商务圈消息失败", e);

				jMessage = JSONMessage.error(e);
			}
		}

		return jMessage;
	}

	@RequestMapping(value = "/comment/list")
	public JSONMessage getCommentList(@RequestParam String messageId,
			@RequestParam(value = "commentId", defaultValue = "") String commentId,
			@RequestParam(value = "pageIndex", defaultValue = "0") int pageIndex,
			@RequestParam(value = "pageSize", defaultValue = "100") int pageSize) {
		JSONMessage jMessage = null;

		try {
			Object data = commentRepository.find(new ObjectId(messageId),
					StringUtil.isEmpty(commentId) ? null : new ObjectId(commentId), pageIndex, pageSize);

			jMessage = JSONMessage.success(null, data);
		} catch (Exception e) {
			logger.error("获取评论列表失败", e);
			jMessage = JSONMessage.error(e);
		}

		return jMessage;
	}

	@RequestMapping(value = "/ids")
	public JSONMessage getFriendsMsgIdList(@RequestParam(value = "userId", defaultValue = "") Integer userId,
			@RequestParam(defaultValue = "") String messageId,
			@RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
		JSONMessage jMessage;

		try {
			ObjectId msgId = !ObjectId.isValid(messageId) ? null : new ObjectId(messageId);
			Object data = msgRepository.getMsgIdList(null == userId ? ReqUtil.getUserId() : userId, 0, msgId, pageSize);
			jMessage = JSONMessage.success(null, data);
		} catch (Exception e) {
			logger.error("获取当前登录用户及其所关注用户的最新商务圈消息Id失败", e);

			jMessage = JSONMessage.error(e);
		}

		return jMessage;
	}

	@RequestMapping(value = "/list")
	public JSONMessage getFriendsMsgList(@RequestParam(value = "userId", defaultValue = "") Integer userId,
			@RequestParam(value = "messageId", defaultValue = "") String messageId,
			@RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
		JSONMessage jMessage;

		try {
			Object data = msgRepository.getMsgList(ReqUtil.getUserId(), null == userId ? ReqUtil.getUserId() : userId,
					!ObjectId.isValid(messageId) ? null : new ObjectId(messageId), pageSize);
			jMessage = JSONMessage.success(null, data);
		} catch (Exception e) {
			logger.error("获取当前登录用户及其所关注用户的最新商务圈消息失败", e);

			jMessage = JSONMessage.error(e);
		}

		return jMessage;
	}

	@RequestMapping(value = "/gift/gbgift")
	public JSONMessage getGiftGroupByGfit(@RequestParam String messageId) {
		JSONMessage jMessage;

		try {
			Object data = giftRepository.findByGift(new ObjectId(messageId));

			jMessage = JSONMessage.success(null, data);
		} catch (Exception e) {
			logger.error("获取礼物列表失败", e);
			jMessage = JSONMessage.error(e);
		}

		return jMessage;
	}

	@RequestMapping(value = "/gift/gbuser")
	public JSONMessage getGiftGroupByUser(@RequestParam String messageId) {
		JSONMessage jMessage;

		try {
			Object data = giftRepository.findByUser(new ObjectId(messageId));

			jMessage = JSONMessage.success(null, data);
		} catch (Exception e) {
			logger.error("获取礼物列表失败", e);
			jMessage = JSONMessage.error(e);
		}

		return jMessage;
	}

	@RequestMapping(value = "/gift/list")
	public JSONMessage getGiftList(@RequestParam String messageId,
			@RequestParam(value = "giftId", defaultValue = "") String giftId,
			@RequestParam(value = "pageIndex", defaultValue = "0") Integer pageIndex,
			@RequestParam(value = "pageSize", defaultValue = "100") Integer pageSize) {
		JSONMessage jMessage;

		try {
			Object data = giftRepository.find(new ObjectId(messageId), new ObjectId(giftId), pageIndex, pageSize);

			jMessage = JSONMessage.success(null, data);
		} catch (Exception e) {
			logger.error("获取礼物列表失败", e);
			jMessage = JSONMessage.error(e);
		}

		return jMessage;
	}
	//获取单条商务圈
	@RequestMapping(value = "/get")
	public JSONMessage getMsgById(@RequestParam String messageId) {
		JSONMessage jMessage;

		if (StringUtil.isEmpty(messageId))
			jMessage = Result.ParamsAuthFail;
		else
			try {
				Object data = msgRepository.get(ReqUtil.getUserId(), new ObjectId(messageId));
				jMessage = JSONMessage.success(null, data);
			} catch (Exception e) {
				logger.error("获取商务圈消息失败", e);

				jMessage = JSONMessage.error(e);
			}

		return jMessage;
	}

	@RequestMapping(value = "/gets")
	public JSONMessage getMsgByIds(@RequestParam String ids) {
		JSONMessage jMessage;

		if (StringUtil.isEmpty(ids))
			jMessage = Result.ParamsAuthFail;
		else
			try {
				Object data = msgRepository.gets(ReqUtil.getUserId(), ids);
				jMessage = JSONMessage.success(null, data);
			} catch (Exception e) {
				logger.error("批量获取商务圈消息失败", e);

				jMessage = JSONMessage.error(e);
			}

		return jMessage;
	}

	@RequestMapping(value = "/praise/list")
	public JSONMessage getPraiseList(@RequestParam String messageId,
			@RequestParam(value = "praiseId", defaultValue = "") String praiseId,
			@RequestParam(value = "pageIndex", defaultValue = "0") Integer pageIndex,
			@RequestParam(value = "pageSize", defaultValue = "100") Integer pageSize) {
		JSONMessage jMessage;

		try {
			if (StringUtil.isEmpty(messageId)) {
				jMessage = Result.ParamsAuthFail;
			} else {
				Object data = praiseRepository.find(new ObjectId(messageId), new ObjectId(praiseId), pageIndex,
						pageSize);

				jMessage = JSONMessage.success(null, data);
			}
		} catch (Exception e) {
			logger.error("获取点赞列表失败", e);

			jMessage = JSONMessage.error(e);
		}

		return jMessage;
	}

	@RequestMapping("/square")
	public JSONMessage getSquareMsgList(@RequestParam(value = "messageId", defaultValue = "") String _id,
			@RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
		ObjectId msgId = !ObjectId.isValid(_id) ? null : new ObjectId(_id);
		Object data = msgRepository.getSquareMsgList(0, msgId, pageSize);

		return JSONMessage.success(null, data);
	}

	@RequestMapping(value = "/user/ids")
	public JSONMessage getUserMsgIdList(@RequestParam(value = "userId", defaultValue = "") Integer userId,
			@RequestParam(value = "messageId", defaultValue = "") String messageId,
			@RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
		Object data = msgRepository.getUserMsgIdList(null == userId ? ReqUtil.getUserId() : userId, userId,
				!ObjectId.isValid(messageId) ? null : new ObjectId(messageId), pageSize);

		return JSONMessage.success(null, data);
	}

	@RequestMapping(value = "/user")
	public JSONMessage getUserMsgList(@RequestParam(value = "userId", defaultValue = "") Integer userId,
			@RequestParam(value = "messageId", defaultValue = "") String messageId,
			@RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
		Object data = msgRepository.getUserMsgList(ReqUtil.getUserId(), null == userId ? ReqUtil.getUserId() : userId,
				!ObjectId.isValid(messageId) ? null : new ObjectId(messageId), pageSize);

		return JSONMessage.success(null, data);
	}

	@RequestMapping("/query")
	public JSONMessage queryByExample(@ModelAttribute MessageExample example) {
		Object data = msgRepository.findByExample(ReqUtil.getUserId(), example);

		return JSONMessage.success(null, data);
	}

}
