package vnm.web.action.program;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PushbackInputStream;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import net.sf.jxls.exception.ParsePropertyException;
import net.sf.jxls.transformer.XLSTransformer;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.POIXMLDocument;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.struts2.ServletActionContext;

import vnm.web.action.general.AbstractAction;
import vnm.web.bean.CellBean;
import vnm.web.bean.TreeGridNode;
import vnm.web.constant.ConstantManager;
import vnm.web.enumtype.FileExtension;
import vnm.web.helper.Configuration;
import vnm.web.helper.R;
import vnm.web.utils.DateUtil;
import vnm.web.utils.LogUtility;
import vnm.web.utils.StringUtil;
import vnm.web.utils.ValidateUtil;
import vnm.web.utils.report.excel.ExcelPOIProcessUtils;
import vnm.web.utils.report.excel.SXSSFReportHelper;

import com.viettel.core.business.PromotionProgramMgr;
import com.viettel.core.common.utils.Constant;
import com.viettel.core.entities.ApParam;
import com.viettel.core.entities.Customer;
import com.viettel.core.entities.CustomerAttribute;
import com.viettel.core.entities.GroupLevel;
import com.viettel.core.entities.GroupMapping;
import com.viettel.core.entities.Product;
import com.viettel.core.entities.ProductGroup;
import com.viettel.core.entities.PromotionCustAttr;
import com.viettel.core.entities.PromotionCustAttrDetail;
import com.viettel.core.entities.PromotionCustomerMap;
import com.viettel.core.entities.PromotionNewcusConfig;
import com.viettel.core.entities.PromotionProgram;
import com.viettel.core.entities.PromotionShopJoin;
import com.viettel.core.entities.PromotionShopMap;
import com.viettel.core.entities.PromotionStaffMap;
import com.viettel.core.entities.Shop;
import com.viettel.core.entities.Staff;
import com.viettel.core.entities.Voucher;
import com.viettel.core.entities.enumtype.ActionSendMailType;
import com.viettel.core.entities.enumtype.ActiveType;
import com.viettel.core.entities.enumtype.ApParamType;
import com.viettel.core.entities.enumtype.AttributeColumnType;
import com.viettel.core.entities.enumtype.AttributeDetailVO;
import com.viettel.core.entities.enumtype.FirstBuyType;
import com.viettel.core.entities.enumtype.KPaging;
import com.viettel.core.entities.enumtype.ProductGroupType;
import com.viettel.core.entities.enumtype.PromotionProgramFilter;
import com.viettel.core.entities.enumtype.PromotionShopMapFilter;
import com.viettel.core.entities.enumtype.PromotionType;
import com.viettel.core.entities.enumtype.ShopDecentralizationSTT;
import com.viettel.core.entities.enumtype.ShopFilter;
import com.viettel.core.entities.enumtype.ShopSpecificType;
import com.viettel.core.entities.enumtype.StaffSpecificType;
import com.viettel.core.entities.filter.BasicFilter;
import com.viettel.core.entities.filter.PromotionCustomerFilter;
import com.viettel.core.entities.filter.PromotionStaffFilter;
import com.viettel.core.entities.vo.CatalogVO;
import com.viettel.core.entities.vo.ChannelTypeVO;
import com.viettel.core.entities.vo.ExMapping;
import com.viettel.core.entities.vo.ExcelPromotionDetail;
import com.viettel.core.entities.vo.ExcelPromotionHeader;
import com.viettel.core.entities.vo.ExcelPromotionUnit;
import com.viettel.core.entities.vo.GroupKM;
import com.viettel.core.entities.vo.GroupLevelVO;
import com.viettel.core.entities.vo.GroupMua;
import com.viettel.core.entities.vo.GroupSP;
import com.viettel.core.entities.vo.LevelMappingVO;
import com.viettel.core.entities.vo.ListGroupKM;
import com.viettel.core.entities.vo.ListGroupMua;
import com.viettel.core.entities.vo.LogInfoVO;
import com.viettel.core.entities.vo.MapMuaKM;
import com.viettel.core.entities.vo.NewLevelMapping;
import com.viettel.core.entities.vo.NewProductGroupVO;
import com.viettel.core.entities.vo.Node;
import com.viettel.core.entities.vo.ObjectVO;
import com.viettel.core.entities.vo.PPConvertVO;
import com.viettel.core.entities.vo.ProductGroupVO;
import com.viettel.core.entities.vo.ProductInfoVO;
import com.viettel.core.entities.vo.PromotionCustAttUpdateVO;
import com.viettel.core.entities.vo.PromotionCustAttVO2;
import com.viettel.core.entities.vo.PromotionCustAttrVO;
import com.viettel.core.entities.vo.PromotionCustomerVO;
import com.viettel.core.entities.vo.PromotionImportGroupLevelDetailNewVO;
import com.viettel.core.entities.vo.PromotionImportGroupLevelNewVO;
import com.viettel.core.entities.vo.PromotionImportGroupLevelProductVO;
import com.viettel.core.entities.vo.PromotionImportGroupLevelVO;
import com.viettel.core.entities.vo.PromotionImportGroupVO;
import com.viettel.core.entities.vo.PromotionImportNewVO;
import com.viettel.core.entities.vo.PromotionImportProductGroupNewVO;
import com.viettel.core.entities.vo.PromotionImportShopNewVO;
import com.viettel.core.entities.vo.PromotionImportShopVO;
import com.viettel.core.entities.vo.PromotionImportSubGroupLevelProductDetailVO;
import com.viettel.core.entities.vo.PromotionImportSubGroupLevelProductVO;
import com.viettel.core.entities.vo.PromotionImportVO;
import com.viettel.core.entities.vo.PromotionNewcusConfigVO;
import com.viettel.core.entities.vo.PromotionProductOpenVO;
import com.viettel.core.entities.vo.PromotionShopMapVO;
import com.viettel.core.entities.vo.PromotionShopQttVO;
import com.viettel.core.entities.vo.PromotionShopVO;
import com.viettel.core.entities.vo.PromotionStaffVO;
import com.viettel.core.entities.vo.SaleCatLevelVO;
import com.viettel.core.entities.vo.ShopVO;
import com.viettel.core.entities.vo.SubLevelMapping;
import com.viettel.core.exceptions.BusinessException;
import com.viettel.core.exceptions.DataAccessException;
import com.viettel.core.memcached.MemcachedUtils;

public class PromotionCatalogAction extends AbstractAction {

	private final List<String> PROMOTION_TYPES = Arrays.asList(PromotionType.ZV01.getValue(), PromotionType.ZV02.getValue(), PromotionType.ZV03.getValue(), PromotionType.ZV04.getValue()
			, PromotionType.ZV05.getValue(), PromotionType.ZV06.getValue(), PromotionType.ZV07.getValue(), PromotionType.ZV08.getValue(), PromotionType.ZV09.getValue()
			, PromotionType.ZV10.getValue(), PromotionType.ZV11.getValue(), PromotionType.ZV12.getValue(), PromotionType.ZV13.getValue(), PromotionType.ZV14.getValue()
			, PromotionType.ZV15.getValue(), PromotionType.ZV16.getValue(), PromotionType.ZV17.getValue(), PromotionType.ZV18.getValue(), PromotionType.ZV19.getValue(),
			PromotionType.ZV20.getValue(), PromotionType.ZV21.getValue(), PromotionType.ZV22.getValue(), PromotionType.ZV23.getValue(), PromotionType.ZV24.getValue());
	
	private final List<String> UNIT = Arrays.asList(R.getResource("ctkm.import.new.le").toUpperCase(), R.getResource("ctkm.import.new.thung").toUpperCase());
	
	private Integer excelType;
	
	private static PromotionCatalogAction action;

	public static synchronized PromotionCatalogAction getInstance() {
		if (action == null) {
			action = new PromotionCatalogAction();
		}
		return action;
	}
	
	private static final long serialVersionUID = 1L;
	public static final Integer ALL_INTEGER_G = -2;
	public static final int CUSTOMER_TYPE = 2;
	public static final int SALE_LEVEL = 3;
	public static final int AUTO_ATTRIBUTE = 1;
	public static final int ZEZO = 0;
	public static final int maxlengthNumber = 9;
	public static final String QUANTITY = "QUANTITY";
	public static final String AMOUNT = "AMOUNT";
	public static final String PERCENT = "PERCENT";
	public static final Integer IS_NPP = 1;
	private String promotionCode;
	private String promotionName;
	private String startDate;
	private String endDate;
	private String typeCode;
	private Boolean canEdit;
	private Integer isEdited;
	private String description;
	private String noticeCode;
	private String descriptionProduct;
	private Integer discountType;
	private Integer rewardType;
	private Boolean firstBuyFlag;
	private String firstBuyType;
	private Integer firstBuyNum;
	private Boolean newCusFlag;
	private Integer newCusNumCycle;
	private Integer checkOpenFullNode;
	private Boolean ontopFlag;
	private Boolean haveRegulatedToStaffFlag;
	private Boolean haveRegulatedToCustFlag;
	private PromotionProgram promotionProgram;
	private PromotionNewcusConfigVO promotionNewcusConfig;
	private List<PromotionCustAttrVO> lstPromotionCustAttrVO;
	private String groupCode;
	private Long mappingId;
	private Long levelMuaId;
	private Long levelKMId;
	private Long levelId;
	private String levelCode;
	private Long levelDetailId;
	private String groupMuaCode;
	private Integer orderLevelMua;
	private String groupKMCode;
	private Integer orderLevelKM;
	private String groupName;
	private Long groupId;
	private Long groupMuaId;
	private Long groupKMId;
	private BigDecimal minQuantity;
	private BigDecimal minAmount;
	private Integer maxQuantity;
	private BigDecimal maxAmount;
	private Boolean multiple;
	private Boolean recursive;
	private Integer quantityUnit;
	private Integer stt;
	private Integer copyNum;
	private List<Long> lstId;
	private List<Long> lstShopIdNPP;
	private List<Long> lstCustomerType;
	private List<Long> lstSaleLevelCatId;
	private List<Integer> lstQtt;
	private List<Boolean> lstEdit;
	private List<Integer> lstObjectType;
	private List<String> lstAttDataInField;
	private List<LevelMappingVO> listLevelMapping;
	private List<Long> listLevelId;
	private List<NewLevelMapping> listNewMapping;
	private List<BigDecimal> lstAmt;
	private List<BigDecimal> lstNum;
	private Integer muaMinQuantity;
	private BigDecimal muaMinAmount;
	private BigDecimal amount;
	private BigDecimal number;
	private Float percentKM;
	private Integer kmMaxQuantity;
	private BigDecimal kmMaxAmount;
	private List<ExMapping> listSubLevelMua;
	private List<ExMapping> listSubLevelKM;
	private List<ExMapping> listSubLevelGroupZV192021;
	private List<ExMapping> listSubLevelConstraintZV07ZV12;
	private Long id;
	private Boolean isVNMAdmin;
	private String excelFileContentType;
	public List<CellBean> lstHeaderError;
	public List<CellBean> lstDetailError;
	public List<CellBean> listUnitError;
	private List<ApParam> lstTypeCode;
	private List<Product> listProduct;
	private List<Integer> listMinQuantity;
	private List<BigDecimal> listMinAmount;
	private List<Integer> listMaxQuantity;
	private List<BigDecimal> listMaxAmount;
	private List<Integer> listOrder;
	private List<String> listProductDetail;
	private List<Float> listPercent;
	private String code;
	private String name;
	private String address;
	private String fromDate;
	private String toDate;
	private String shopCode;
	private String lstTypeId;
	private Long promotionId;
	private Long shopId;
	private Integer quantity;
	private Integer status;
	private Integer promotionStatus;
	private Integer proType;
	private String promotionType;
	private Integer quantiMonthNewOpen;
	private Integer fromLevel;
	private Integer toLevel;
	private File excelFile;
	private List<ProductGroup> lstGroupSale;
	private List<ProductGroup> lstGroupFree;
	private List<NewProductGroupVO> lstGroupNew;
	private List<PPConvertVO> listConvertGroup;
	private List<PromotionProductOpenVO> listProductOpen;
	private List<GroupLevelVO> lstLevel;
	private List<PromotionShopQttVO> lstShopQttAdd;
	private long indexMua = 1;
	private long indexKM = 1000001;
	private Boolean isShowCompleteDefinePromo;
	private Boolean isAllocationPromotionShop;
	private Boolean isDiscount;
	private Boolean isReward;
	private Integer ontop;
	private String lstBrandId;
	private String lstCategoryId;
	private String lstSubCategoryId;
	private String numberNotify;
	private Integer futureDate;
	private String fromApplyDate;
	private String toApplyDate;
	private List<CatalogVO> lstStatus;
	private Boolean checkPer;
	private Boolean flagExpire;
	
	@Override
	public void prepare() throws Exception {
		super.prepare();
		//displayProgramVNMMgr = (DisplayProgramVNMMgr) context.getBean("displayProgramVNMMgr");
		//Staff loginStaff = getStaffByCurrentUser();
		isVNMAdmin = StaffSpecificType.VIETTEL_ADMIN.getValue().equals(currentUser.getStaffRoot().getObjectType());
		staff = staffMgr.getStaffById(currentUser.getUserId());
	}
	
	@Override
	public String execute() throws Exception {
		resetToken(result);
		try {
			lstTypeCode = apParamMgr.getListApParam(ApParamType.PROMOTION, ActiveType.RUNNING);
			proType = ConstantManager.PROMOTION_AUTO;
			ApParam appPa = apParamMgr.getApParamByCode(ApParamType.LIST_STATUS_PROMOTION.getValue(), null);
			/*ApParamFilter filter = new ApParamFilter();
			filter.setApParamCode(ApParamType.LIST_STATUS_PROMOTION.getValue());
			List<ApParam> lstAppParam = apParamMgr.getListApParamByFilter(filter);*/
			List<String> lstStaffTypeId = new ArrayList<String>();
			lstStatus = new ArrayList<>();
			String[] arrStaffTypeId = appPa.getValue().split(",");
			for (String ap : arrStaffTypeId) {
				lstStaffTypeId.add(ap);
			}
			if (lstStaffTypeId.contains(staff.getStaffType().getId().toString())) {
				checkPer = true;
				CatalogVO c = new CatalogVO();
				/*c.setCode("-2");
				c.setName("Tất cả");
				lstStatus.add(c);*/
				/*c = new CatalogVO();
				c.setCode("1");
				c.setName("Hoạt động");
				lstStatus.add(c);*/
				c.setCode("8");
				c.setName("Hết hạn");
				lstStatus.add(c);
			} else {
				checkPer = false;
				CatalogVO c = new CatalogVO();
				/*c.setCode("-2");
				c.setName("Tất cả");
				lstStatus.add(c);
				c = new CatalogVO();*/
				c.setCode("0");
				c.setName("Tạm ngưng");
				lstStatus.add(c);
				/*c = new CatalogVO();
				c.setCode("1");
				c.setName("Hoạt động");
				lstStatus.add(c);*/
				c = new CatalogVO();
				c.setCode("2");
				c.setName("Dự thảo");
				lstStatus.add(c);
				c = new CatalogVO();
				c.setCode("8");
				c.setName("Hết hạn");
				lstStatus.add(c);
			}
			
		} catch (Exception e) {
			LogUtility.logErrorStandard(e, R.getResource("web.log.message.error", "vnm.web.action.program.PromotionCatalogAction.execute"), createLogErrorStandard(actionStartTime));
		}
		return SUCCESS;
	}

	/**
	 * Tim kiem CTKM
	 * 
	 * @author lacnv1
	 * @since Aug 21, 2014
	 */
	public String search() throws Exception {
		actionStartTime = new Date();
		result.put("rows", new ArrayList<PromotionProgram>());
		result.put("total", 0);
		ApParam appPa = apParamMgr.getApParamByCode(ApParamType.LIST_STATUS_PROMOTION.getValue(), null);
		List<String> lstStaffTypeId = new ArrayList<String>();
		String[] arrStaffTypeId = appPa.getValue().split(",");
		for (String ap : arrStaffTypeId) {
			lstStaffTypeId.add(ap);
		}
		if (lstStaffTypeId.contains(staff.getStaffType().getId().toString())) {
			checkPer = true;
		} else {
			checkPer = false;
		}
		try {
			staff = getStaffByCurrentUser();
			if (staff == null || staff.getShop() == null || staff.getStaffType() == null) {
				return JSON;
			}
			result.put("page", page);
			result.put("max", max);

			PromotionProgramFilter filter = new PromotionProgramFilter();
			KPaging<PromotionProgram> kPaging = new KPaging<PromotionProgram>();
			kPaging.setPageSize(rows);
			kPaging.setPage(page - 1);
			filter.setkPaging(kPaging);

			Date fDate;
			Date tDate = null;
			filter.setCheckPer(checkPer);
			if (checkPer) {
				filter.setFlagRunning(true);
			} else {
				filter.setFlagRunning(false);
			}
			if (!StringUtil.isNullOrEmpty(fromDate)) {
				fDate = DateUtil.parse(fromDate, DateUtil.DATE_FORMAT_DDMMYYYY);
				filter.setFromDate(fDate);
			}
			if (!StringUtil.isNullOrEmpty(toDate)) {
				tDate = DateUtil.parse(toDate, DateUtil.DATE_FORMAT_DDMMYYYY);
				filter.setToDate(tDate);
			}
			if (!StringUtil.isNullOrEmpty(numberNotify)) {
				filter.setNumberNotify(numberNotify);
			}
			List<String> temp = new ArrayList<String>();
			if (!StringUtil.isNullOrEmpty(lstTypeId)) {
				if (lstTypeId.indexOf("-1") > -1) {
					temp = null;
				} else {
					String[] lstTmp = lstTypeId.split(",");
					if (lstTmp.length > 0) {
						Integer size = lstTmp.length;
						for (int i = 0; i < size; i++) {
							ApParam apParam = apParamMgr.getApParamById(Long.valueOf(lstTmp[i].trim()));
							if (apParam != null) {
								temp.add(apParam.getApParamCode());
							}
						}
					}
				}
			}
			filter.setLstType(temp);

			if (ontop != null && ontop == 1) {
				filter.setOntop(ontop);
			}
			//			ActiveType at = ActiveType.RUNNING;
			if (status == null) {
				status = ActiveType.RUNNING.getValue();
			}
			if (!ALL_INTEGER_G.equals(status)) {
				ActiveType at = ActiveType.parseValue(status);
				/**
				 * @author kieupp Them dieu kien tim kiem dang het han chuong
				 *         trinh khuyen mai voi Chuong trinh KM hoat dong nhung
				 *         to_date nho hon ngay hien tai dat bien flag den biet
				 *         dang tim kiem dang o o dang het han
				 */
				if (ActiveType.HET_HAN.equals(at)) {
					Date sysDate = commonMgr.getSysDate();
					if (tDate != null) {
						if (DateUtil.compareDateWithoutTime(tDate, sysDate) > 0) {
							tDate = DateUtil.getYesterday(sysDate);
						}
					} else {
						tDate = DateUtil.getYesterday(sysDate);
					}
					filter.setToDate(tDate);

					filter.setFlagExpire(true);
					at = ActiveType.RUNNING;
					filter.setStatus(at);
				} else {
					filter.setStatus(at);
				}
			}
			if (StringUtil.isNullOrEmpty(shopCode)) {
				filter.setShopCode(currentUser.getShopRoot().getShopCode());
				filter.setCreateUser(currentUser.getStaffRoot().getStaffCode());
				if (isVNMAdmin) {
					filter.setIsVNM(true);
				}
			} else {
				filter.setShopCode(shopCode);
			}
			filter.setPpCode(code);
			filter.setPpName(name);
			if (proType != null && proType.intValue() == ConstantManager.PROMOTION_AUTO) {
				filter.setIsAutoPromotion(true);
			} else {
				filter.setIsAutoPromotion(false);
			}
			filter.setStrListShopId(getStrListShopId());
			ObjectVO<PromotionProgram> objVO = promotionProgramMgr.getListPromotionProgram(filter);

			result.put(ERROR, false);
			result.put("rows", objVO.getLstObject());
			result.put("total", objVO.getkPaging().getTotalRows());
		} catch (Exception ex) {
			LogUtility.logErrorStandard(ex, R.getResource("web.log.message.error", "vnm.web.action.program.PromotionCatalogAction.search"), createLogErrorStandard(actionStartTime));
			result.put("errMsg", ValidateUtil.getErrorMsg(ConstantManager.ERR_SYSTEM));
			result.put(ERROR, true);
			return JSON;
		}
		return JSON;
	}

	public String update() {
		List<PromotionShopMap> lstCheckPromotionShopMap = new ArrayList<PromotionShopMap>();
		List<PromotionShopJoin> lstCheckPromotionShopJoin = new ArrayList<PromotionShopJoin>();
		promotionCode = promotionCode.trim();
		promotionName = promotionName.trim();
		actionStartTime = new Date();
		resetToken(result);
		try {
			Date sysDate = commonMgr.getSysDate();
			Date startApplyDate = null;
			Date endApplyDate = null;
			if (!StringUtil.isNullOrEmpty(fromApplyDate)) {
				startApplyDate = DateUtil.parse(fromApplyDate, DateUtil.DATE_FORMAT_DDMMYYYY);
			}
			if (!StringUtil.isNullOrEmpty(toApplyDate)) {
				endApplyDate = DateUtil.parse(toApplyDate, DateUtil.DATE_FORMAT_DDMMYYYY);
			}
			if (promotionId != null && promotionId > 0) {
				Date __startDate = null;
				Date __endDate = null;
				if (!StringUtil.isNullOrEmpty(startDate)) {
					__startDate = DateUtil.parse(startDate, DateUtil.DATE_FORMAT_DDMMYYYY);
				}
				if (!StringUtil.isNullOrEmpty(endDate)) {
					__endDate = DateUtil.parse(endDate, DateUtil.DATE_FORMAT_DDMMYYYY);
				}
				PromotionProgram promotionProgram = promotionProgramMgr.getPromotionProgramById(promotionId);
				if (promotionProgram == null) {
					result.put(ERROR, true);
					result.put("errMsg", ValidateUtil.getErrorMsg(ConstantManager.ERR_NOT_EXIST_DB, "CTKM"));
					return SUCCESS;
				}

				if (ActiveType.WAITING.equals(ActiveType.parseValue(status)) && ActiveType.RUNNING.getValue().equals(promotionProgram.getStatus().getValue())) {
					result.put("errMsg", Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "promotion.program.status.error"));
					result.put(ERROR, true);
					return SUCCESS;
				}

				errMsg = ValidateUtil.validateField(promotionName, "catalog.promotion.name", 500, ConstantManager.ERR_REQUIRE, ConstantManager.ERR_EXIST_SPECIAL_CHAR_IN_SPECIAL, ConstantManager.ERR_MAX_LENGTH);
				if (StringUtil.isNullOrEmpty(errMsg)) {
					errMsg = ValidateUtil.validateField(description, "common.description", null, ConstantManager.ERR_EXIST_SPECIAL_CHAR_IN_SPECIAL);
				}
				if (!StringUtil.isNullOrEmpty(errMsg)) {
					result.put("errMsg", errMsg);
					result.put(ERROR, true);
					return SUCCESS;
				}
				if (!ActiveType.STOPPED.equals(ActiveType.parseValue(status))) {
					if (ActiveType.WAITING.equals(promotionProgram.getStatus())) {
						// Du thao
						if (Boolean.TRUE.equals(firstBuyFlag)) {
							promotionProgram.setFirstBuyFlag(PromotionProgramMgr.FIRST_BUY_CHECK);
							FirstBuyType type = null;
							if (firstBuyType != null) {
								type = FirstBuyType.parseValue(firstBuyType);
							}
							if (type != null) {
								promotionProgram.setFirstBuyType(type);
							} else {
								result.put("errMsg", Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "promotion.program.first.buy.type.no"));
								result.put(ERROR, true);
								return SUCCESS;
							}
							if (firstBuyNum != null && firstBuyNum > -1) {
								promotionProgram.setFirstBuyNum(firstBuyNum);
							} else {
								result.put("errMsg", Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "promotion.program.first.buy.num.no"));
								result.put(ERROR, true);
								return SUCCESS;
							}
						} else {
							promotionProgram.setFirstBuyFlag(null);
							promotionProgram.setFirstBuyType(null);
							promotionProgram.setFirstBuyNum(null);
						}
						if (Boolean.TRUE.equals(newCusFlag)) {
							promotionProgram.setNewCusFlag(PromotionProgramMgr.NEW_CUS_CHECK);
							if (newCusNumCycle != null && newCusNumCycle > -1) {
								promotionProgram.setNewCusNumCycle(newCusNumCycle);
							} else {
								result.put("errMsg", Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "promotion.program.new.cus.num.cycle.no"));
								result.put(ERROR, true);
								return SUCCESS;
							}
						} else {
							promotionProgram.setNewCusFlag(null);
							promotionProgram.setNewCusNumCycle(null);
						}
						/*
						 * if (Boolean.TRUE.equals(ontopFlag)) {
						 * promotionProgram.setOntopFlag(PromotionProgramMgr.
						 * ONTOP_CHECK); } else {
						 * promotionProgram.setOntopFlag(null); }
						 */											
					} else if (ActiveType.RUNNING.equals(promotionProgram.getStatus()) && DateUtil.compareDateWithoutTime(promotionProgram.getFromDate(), sysDate) <= 0 && (promotionProgram.getToDate() == null || DateUtil.compareDateWithoutTime(
							promotionProgram.getToDate(), sysDate) >= 0)) {
						// CTKM dang hoat dong
						if (PromotionProgramMgr.FIRST_BUY_CHECK.equals(promotionProgram.getFirstBuyFlag())) {
							if (firstBuyNum != null && firstBuyNum > -1) {
								if (promotionProgram.getFirstBuyNum() != null && firstBuyNum < promotionProgram.getFirstBuyNum()) {
									result.put("errMsg", Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "promotion.program.first.buy.num.more.than.old"));
									result.put(ERROR, true);
									return SUCCESS;
								}
								promotionProgram.setFirstBuyNum(firstBuyNum);
							} else {
								result.put("errMsg", Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "promotion.program.first.buy.num.no"));
								result.put(ERROR, true);
								return SUCCESS;
							}
						}
					}
				}
				if (ActiveType.WAITING.equals(promotionProgram.getStatus()) && ActiveType.RUNNING.equals(ActiveType.parseValue(status))) {
					if (__startDate != null && DateUtil.compareDateWithoutTime(__startDate, sysDate) < 0) {
						result.put("errMsg", Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "promotion.program.transfer.fromdate.less.than.sysdate"));
						result.put(ERROR, true);
						return SUCCESS;
					}
					if (__startDate != null && __endDate != null && DateUtil.compareDateWithoutTime(__startDate, __endDate) > 0) {
						result.put("errMsg", Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "promotion.program.transfer.fromdate.less.than.enddate"));
						result.put(ERROR, true);
						return SUCCESS;
					}
					List<PromotionShopVO> listShopMap = promotionProgramMgr.getShopTreeInPromotionProgram(currentUser.getShopRoot().getShopId(), promotionProgram.getId(), null, null, null);
					if (listShopMap.isEmpty()) {
						result.put("errMsg", Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "promotion.program.shop.map.is.empty"));
						result.put(ERROR, true);
						return SUCCESS;
					}
					List<NewProductGroupVO> __listGroup = promotionProgramMgr.getListNewProductGroupByPromotionId(promotionProgram.getId());
					Boolean hasMapping = false;
					if (!__listGroup.isEmpty()) {
						if (__listGroup.get(0).getStt() == null || __listGroup.get(0).getStt() == 0) {
							for (int i = 0; i < __listGroup.size(); i++) {
								if (__listGroup.get(i).getStt() != null && __listGroup.get(i).getStt() != 0) {
									result.put("errMsg", Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "promotion.product.group.order.is.not.null", __listGroup.get(i).getGroupMuaCode()));
									result.put(ERROR, true);
									return SUCCESS;
								}
							}
						} else {
							List<Integer> arrOrder = new ArrayList<Integer>();
							for (int i = 0; i < __listGroup.size(); i++) {
								if (__listGroup.get(i).getStt() == null || __listGroup.get(i).getStt() == 0) {
									result.put("errMsg", Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "promotion.product.group.order.is.null", __listGroup.get(i).getGroupMuaCode()));
									result.put(ERROR, true);
									return SUCCESS;
								} else if (arrOrder.indexOf(__listGroup.get(i).getStt()) != -1) {
									result.put("errMsg", Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "promotion.product.group.order.is.null", __listGroup.get(i).getGroupMuaCode()));
									result.put(ERROR, true);
									return SUCCESS;
								} else {
									arrOrder.add(__listGroup.get(i).getStt());
								}
							}
						}
						Map<Long, Boolean> mapCheckCondition = new HashMap<Long, Boolean>();
						boolean flagProduct = false;
						for (NewProductGroupVO groupProduct : __listGroup) {
							List<NewLevelMapping> listMapping = promotionProgramMgr.getListMappingLevel(groupProduct.getGroupMuaId(), groupProduct.getGroupKMId(), null, null).getLstObject();
							if (!listMapping.isEmpty()) {
								hasMapping = true;
								//set defaul value
								ProductGroup pg = promotionProgramMgr.getrecursiveBypromotionId(promotionId);
								int recursive = pg.getRecursive();
								for (NewLevelMapping mapping : listMapping) {
									mapCheckCondition.put(mapping.getLevelMuaId(), false);
								}
								String lsta = "";
//								if (listMapping.get(0).getListExLevelMua().get(0).getTextCode()==null){
//									lsta = listMapping.get(0).getListExLevelMua().get(1).getTextCode();
//								}else{
//									lsta = listMapping.get(0).getListExLevelMua().get(0).getTextCode();
//								}
								if(listMapping.get(0).getListExLevelMua() != null) {
									if(listMapping.get(0).getListExLevelMua().size() > 0 && StringUtil.isNullOrEmpty(listMapping.get(0).getListExLevelMua().get(0).getTextCode())) {
										lsta = listMapping.get(0).getListExLevelMua().get(0).getTextCode();
									} else if(listMapping.get(0).getListExLevelMua().size() > 1 && StringUtil.isNullOrEmpty(listMapping.get(0).getListExLevelMua().get(1).getTextCode())){
										lsta = listMapping.get(0).getListExLevelMua().get(1).getTextCode();
									}
								}
								
								
								for (NewLevelMapping mapping : listMapping) {
									if (PromotionType.ZV20.getValue().equals(promotionProgram.getType()) || PromotionType.ZV21.getValue().equals(promotionProgram.getType())){
										if (recursive == 1){
											if (mapping.getListExLevelMua() != null && mapping.getListExLevelMua().size() > 0) {
												
												for (ExMapping ex : mapping.getListExLevelMua()) {
													if (ex.getCondition() != null) {
														if (ex.getListSubLevel() != null && ex.getListSubLevel().size() > 0) {
															mapCheckCondition.put(mapping.getLevelMuaId(), true);
														}
													}
													for (SubLevelMapping subM: ex.getListSubLevel()){
														if (!StringUtil.isNullOrEmpty(subM.getProductCode())&&lsta!=null&&!lsta.contains(subM.getProductCode())){
															flagProduct = true;
														}
													}
												}
											}
										}
									}
									
									if (mapping.getListExLevelMua() == null || mapping.getListExLevelMua().isEmpty() || mapping.getListExLevelKM() == null || mapping.getListExLevelKM().isEmpty()) {
										result.put("errMsg", Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "promotion.has.no.sub.level.or.product.detail"));
										result.put(ERROR, true);
										return SUCCESS;
									}
									// nếu loại CTKM là ZV03, 06,09,12,15,18,21,24 thì phải có sp km
									if (PromotionType.ZV03.getValue().equals(promotionProgram.getType()) || PromotionType.ZV06.getValue().equals(promotionProgram.getType()) || PromotionType.ZV09.getValue().equals(promotionProgram.getType())
											|| PromotionType.ZV12.getValue().equals(promotionProgram.getType()) || PromotionType.ZV15.getValue().equals(promotionProgram.getType()) || PromotionType.ZV18.getValue().equals(promotionProgram.getType())
											|| PromotionType.ZV21.getValue().equals(promotionProgram.getType()) || PromotionType.ZV24.getValue().equals(promotionProgram.getType())) {
										if (mapping.getListExLevelKM() != null && mapping.getListExLevelKM().size() > 0) {
											for (ExMapping exMapping : mapping.getListExLevelKM()) {
												if (exMapping == null || exMapping.getListSubLevel() == null || exMapping.getListSubLevel().size() <= 0) {
													result.put("errMsg", Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "promotion.has.no.product.km.detail"));
													result.put(ERROR, true);
													return SUCCESS;
												}
											}
										}
									}
								}
							}
							// Check conditon
							int countCondition = 0;
							if (mapCheckCondition != null){
								for (Map.Entry<Long, Boolean> entry : mapCheckCondition.entrySet()) {
									if (entry.getValue()) {
										countCondition++;
									}
								}
								if (countCondition > 0 && countCondition < mapCheckCondition.size()) {
									result.put("errMsg", Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "promotion.program.group.level.code.maping.net.same"));
									result.put(ERROR, true);
									return SUCCESS;
								}
								if (flagProduct == true){
									result.put("errMsg", Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "promotion.program.group.level.code.maping.net.same"));
									result.put(ERROR, true);
									return SUCCESS;
								}
							}
						}
						if (!hasMapping) {
							result.put("errMsg", Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "promotion.product.mapping.is.empty"));
							result.put(ERROR, true);
							return SUCCESS;
						}
					} else {
						result.put("errMsg", Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "promotion.product.group.is.empty"));
						result.put(ERROR, true);
						return SUCCESS;
					}

					/*
					 * //check level da phan bo het cho cac co cau hay chua
					 * List<GroupLevel> listLevelNotMapping =
					 * promotionProgramMgr
					 * .getListLevelNotInMapping(promotionProgram.getId());
					 * if(!listLevelNotMapping.isEmpty()) { result.put("errMsg",
					 * Configuration
					 * .getResourceString(ConstantManager.VI_LANGUAGE,
					 * "promotion.product.has.exist.level.not.mapping"));
					 * result.put(ERROR, true); return SUCCESS; }
					 */

					if (PromotionType.ZV26.getValue().equals(promotionProgram.getType())) {
						listConvertGroup = promotionProgramMgr.listPromotionProductConvertVO(promotionProgram.getId(), null);
						boolean isHasRoot = false;
						boolean isHasMore2 = false;
						for (int k = 0; k < listConvertGroup.size(); k++) {
							for (int kk = 0; kk < listConvertGroup.get(k).getListDetail().size(); kk++) {
								if (kk > 0) {
									isHasMore2 = true;
								}
								if (listConvertGroup.get(k).getListDetail().get(kk).getIsSourceProduct() == 1) {
									isHasRoot = true;
								}
							}
							if (!isHasMore2) {
								result.put("errMsg", Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "promotion.product.product.convert.error.1", listConvertGroup.get(k).getName()));
								result.put(ERROR, true);
								return SUCCESS;
							}
							if (!isHasRoot) {
								result.put("errMsg", Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "promotion.product.product.convert.error.2", listConvertGroup.get(k).getName()));
								result.put(ERROR, true);
								return SUCCESS;
							}
						}
					}

					if (PromotionType.ZV27.getValue().equals(promotionProgram.getType())) {
						listProductOpen = promotionProgramMgr.listPromotionProductOpenVO(promotionId);
						if (listProductOpen.isEmpty()) {
							result.put("errMsg", Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "promotion.product.product.customer.open.new"));
							result.put(ERROR, true);
							return SUCCESS;
						}
					}

					//TungMT comment code
					//					if(PromotionType.ZV25.getValue().equals(promotionProgram.getType()) || PromotionType.ZV26.getValue().equals(promotionProgram.getType()) || PromotionType.ZV27.getValue().equals(promotionProgram.getType())) {
					//						Boolean checkCoCau = promotionProgramMgr.checkCoCau(promotionProgram.getId());
					//						if(!checkCoCau) {
					//							result.put("errMsg", Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "promotion.product.co.cau.trung.error"));
					//							result.put(ERROR, true);
					//							return SUCCESS;
					//						}
					//					}
					//					

					//check sp ctkm co ton tai trong cac ctkm khac dang hoat dong ko?
					//					PromotionProgram existPromotion = promotionProgramMgr.checkProductExistInOrPromotion(promotionProgram.getId());
					//					if (existPromotion != null) {
					//						result.put("errMsg", Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "promotion.product.exists.in.other.promotion", existPromotion.getPromotionProgramCode()));
					//						result.put(ERROR, true);
					//						return SUCCESS;
					//					}

					lstCheckPromotionShopMap = promotionProgramMgr.getListPromotionChildShopMapWithShopAndPromotionProgram(currentUser.getShopRoot().getShopId(), promotionId);
					lstCheckPromotionShopJoin = promotionProgramMgr.getListPromotionChildShopJoinWithShopAndPromotionProgram(currentUser.getShopRoot().getShopId(), promotionId);
					String error = "";
					// lay cau hinh bat buoc cac node NPP phai duoc phan bo
					List<ApParam> allocationPromotionShopConfigs = apParamMgr.getListApParam(ApParamType.ALLOCATION_PROMOTION_SHOP, ActiveType.RUNNING);
					isAllocationPromotionShop = (allocationPromotionShopConfigs == null || allocationPromotionShopConfigs.size() == 0 || Constant.ONE_TEXT.equals(allocationPromotionShopConfigs.get(0).getValue()));
					if (isAllocationPromotionShop) {
						error = validateQuantityAmountNumPromotionShop(lstCheckPromotionShopMap);
						if (!StringUtil.isNullOrEmpty(error)) {
							result.put("errMsg", error);
							result.put(ERROR, true);
							return SUCCESS;
						}
					}
					error = validateAllocateParentChildPromotionShop(lstCheckPromotionShopMap, lstCheckPromotionShopJoin);
					if (!StringUtil.isNullOrEmpty(error)) {
						result.put("errMsg", error);
						result.put(ERROR, true);
						return SUCCESS;
					}
					error = validatePromotionShop(lstCheckPromotionShopMap, lstCheckPromotionShopJoin);
					if (!StringUtil.isNullOrEmpty(error)) {
						result.put("errMsg", error);
						result.put(ERROR, true);
						return SUCCESS;
					}
				}
				if (ActiveType.WAITING.equals(promotionProgram.getStatus())) {
					if (Boolean.TRUE.equals(ontopFlag)) {
						promotionProgram.setOntopFlag(PromotionProgramMgr.ONTOP_CHECK);
					} else {
						promotionProgram.setOntopFlag(null);
					}
				}
				if (ActiveType.WAITING.equals(promotionProgram.getStatus())) {
					if (Boolean.TRUE.equals(haveRegulatedToStaffFlag)) {
						promotionProgram.setHaveRegulatedToStaff(PromotionProgramMgr.HAVE_REGULATED_TO_STAFF);
					} else {
						promotionProgram.setHaveRegulatedToStaff(PromotionProgramMgr.HAVE_REGULATED_TO_STAFF_NO);
					}
				}
				if (ActiveType.WAITING.equals(promotionProgram.getStatus())) {
					if (Boolean.TRUE.equals(haveRegulatedToCustFlag)) {
						promotionProgram.setHaveRegulatedToCust(PromotionProgramMgr.HAVE_REGULATED_TO_CUST);
					} else {
						promotionProgram.setHaveRegulatedToCust(PromotionProgramMgr.HAVE_REGULATED_TO_CUST_NO);
					}
				}
				if (PromotionType.ZV19.getValue().equals(promotionProgram.getType()) || PromotionType.ZV20.getValue().equals(promotionProgram.getType()) || PromotionType.ZV21.getValue().equals(promotionProgram.getType()) || PromotionType.ZV22
						.getValue().equals(promotionProgram.getType()) || PromotionType.ZV23.getValue().equals(promotionProgram.getType()) || PromotionType.ZV24.getValue().equals(promotionProgram.getType())) {
					promotionProgram.setOntopFlag(null);
				}

				promotionProgram.setPromotionProgramName(promotionName);
				noticeCode = noticeCode.trim();
				noticeCode = noticeCode.toUpperCase();
				promotionProgram.setNoticeCode(noticeCode);
				descriptionProduct = descriptionProduct.trim();
				promotionProgram.setDescriptionProduct(descriptionProduct);
				description = description.trim();
				promotionProgram.setDescription(description);
				if (discountType == null) {
					promotionProgram.setDiscountType(0);
					promotionProgram.setRewardType(rewardType);
				} else {
					promotionProgram.setDiscountType(discountType);
					promotionProgram.setRewardType(rewardType);
				}

				if (ActiveType.WAITING.getValue().equals(promotionProgram.getStatus().getValue()) || ActiveType.RUNNING.getValue().equals(promotionProgram.getStatus().getValue())) {
					promotionProgram.setToDate(__endDate);
					//					quangntp - 20171003 - Edit start day with running promotion program
					promotionProgram.setFromDate(__startDate);
					/*PromotionShopMap promotionShop = promotionProgramMgr.getPromotionShopMapById(promotionId);
					if (promotionShop != null) {
						promotionShop.setFromDate(__startDate);
						promotionShop.setToDate(__endDate);
					}*/
				}
				if (ActiveType.WAITING.getValue().equals(promotionProgram.getStatus().getValue())) {
					promotionProgram.setFromDate(__startDate);
					promotionProgram.setIsEdited(isEdited);
					promotionProgram.setQuantiMonthNewOpen(quantiMonthNewOpen);
				}
				if (status != null && ActiveType.WAITING.getValue().equals(promotionProgram.getStatus().getValue())) {
					promotionProgram.setStatus(ActiveType.parseValue(status));
				}
				
				//voucher = 2
				if(promotionProgram.getRewardType() != null && promotionProgram.getRewardType().equals(2)) {
					if(StringUtil.isNullOrEmpty(fromApplyDate)) {
						result.put("errMsg", Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "common.empty.date", R.getResource("common.date.fromapplydate")));
						result.put(ERROR, true);
						return SUCCESS;
					}
					if(DateUtil.checkInvalidFormatDate(fromApplyDate)) {
						result.put("errMsg", Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "common.invalid.format.date", R.getResource("common.date.fromapplydate")));
						result.put(ERROR, true);
						return SUCCESS;
					}
					
					if(ActiveType.WAITING.getValue().equals(promotionProgram.getStatus().getValue())) {
						if(DateUtil.compareDateWithoutTime(startApplyDate, sysDate) == -1) {
							result.put("errMsg", Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "common.date.greater.currentdate", R.getResource("common.date.fromapplydate")));
							result.put(ERROR, true);
							return SUCCESS;
						}
					}
												
					if(DateUtil.compareDateWithoutTime(startApplyDate, __startDate) == -1) {
						result.put("errMsg", Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "common.compare.error.less.or.equal.tow.param", R.getResource("common.date.fromdate"), R.getResource("common.date.fromapplydate")));
						result.put(ERROR, true);
						return SUCCESS;
					}					
					if(!StringUtil.isNullOrEmpty(toApplyDate) && DateUtil.checkInvalidFormatDate(toApplyDate)) {
						result.put("errMsg", Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "common.invalid.format.date", R.getResource("common.date.toapplydate")));
						result.put(ERROR, true);
						return SUCCESS;
					}
					if(endApplyDate != null && DateUtil.compareDateWithoutTime(endApplyDate, startApplyDate) == -1) {
						result.put("errMsg", Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "common.compare.error.less.or.equal.tow.param", R.getResource("common.date.fromapplydate"), R.getResource("common.date.toapplydate")));
						result.put(ERROR, true);
						return SUCCESS;
					}
					
					if(ActiveType.WAITING.getValue().equals(promotionProgram.getStatus().getValue())) {
						if(endApplyDate != null && DateUtil.compareDateWithoutTime(endApplyDate, sysDate) == -1) {
							result.put("errMsg", Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "common.date.greater.currentdate", R.getResource("common.date.toapplydate")));
							result.put(ERROR, true);
							return SUCCESS;
						}
					
					}
					
					if(ActiveType.RUNNING.getValue().equals(promotionProgram.getStatus().getValue())) {
						if(endApplyDate != null && DateUtil.compareDateWithoutTime(endApplyDate, sysDate) == -1) {
							if(promotionProgram.getToApplyDate() == null || DateUtil.compareDateWithoutTime(endApplyDate, promotionProgram.getToApplyDate()) != 0) {
								result.put("errMsg", Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "common.date.greater.currentdate", R.getResource("common.date.toapplydate")));
								result.put(ERROR, true);
								return SUCCESS;
							}
						}
					}
					
					
					
					// dữ liệu họp lệ
					if(promotionProgram.getStatus().equals(ActiveType.WAITING)) {
						promotionProgram.setFromApplyDate(startApplyDate);
						promotionProgram.setToApplyDate(endApplyDate);
					} else if(!(promotionProgram.getStatus().equals(ActiveType.RUNNING) && promotionProgram.getToDate() != null && DateUtil.compareDateWithoutTime(promotionProgram.getToDate(), sysDate) == -1)){
						// chuong trinh dang hoat dong
						if(DateUtil.compareDateWithoutTime(promotionProgram.getFromApplyDate(), sysDate) == 1) {
							promotionProgram.setFromApplyDate(startApplyDate);
						}
						
						if(endApplyDate != null && DateUtil.compareDateWithoutTime(endApplyDate, sysDate) >= 0) {
							promotionProgram.setToApplyDate(endApplyDate);
						}						
					}
				}

				promotionProgram.setStatus(ActiveType.parseValue(status));
				
				promotionProgramMgr.updatePromotionProgram(promotionProgram, getLogInfoVO());
				List<Voucher> voucher = voucherMgr.getListVoucherByPromotion(promotionProgram.getId());
				voucherMgr.updateVoucherByPromotion(voucher, promotionProgram);
				//update promotion newcus config
				PromotionNewcusConfig promotionNewcusConfigs = promotionNewcusConfigMgr.getPromNewcusConfigByPromPrgId(promotionProgram.getId());
				if (promotionNewcusConfigs != null) {
					if (ActiveType.STOPPED.getValue().equals(promotionProgram.getNewCusFlag())) {//if uncheck newcus => deleted row
						promotionNewcusConfigMgr.deletePromotionNewcusConfig(promotionNewcusConfigs, getLogInfoVO());
					} else {//update 
						promotionNewcusConfigs.setBrandIds(lstBrandId);
						promotionNewcusConfigs.setCatIds(lstCategoryId);
						promotionNewcusConfigs.setSubCatIds(lstSubCategoryId);
						promotionNewcusConfigMgr.updatePromotionNewcusConfig(promotionNewcusConfigs, getLogInfoVO());
					}
				} else {
					PromotionNewcusConfig promotionNewcusConfig = new PromotionNewcusConfig();
					promotionNewcusConfig.setBrandIds(lstBrandId);
					promotionNewcusConfig.setCatIds(lstCategoryId);
					promotionNewcusConfig.setSubCatIds(lstSubCategoryId);
					promotionNewcusConfig.setPromotionProgram(promotionProgram);
					promotionNewcusConfigMgr.createPromotionNewcusConfig(promotionNewcusConfig, getLogInfoVO());
				}
				result.put(ERROR, false);
				result.put("promotionId", promotionProgram.getId());
			} else {
				errMsg = ValidateUtil.validateField(promotionCode, "catalog.promotion.code", 100, ConstantManager.ERR_REQUIRE, ConstantManager.ERR_EXIST_SPECIAL_CHAR_IN_CODE, ConstantManager.ERR_MAX_LENGTH);
				if (StringUtil.isNullOrEmpty(errMsg)) {
					errMsg = ValidateUtil.validateField(promotionName, "catalog.promotion.name", 500, ConstantManager.ERR_REQUIRE, ConstantManager.ERR_EXIST_SPECIAL_CHAR_IN_SPECIAL, ConstantManager.ERR_MAX_LENGTH);
				}
				if (StringUtil.isNullOrEmpty(errMsg)) {
					errMsg = ValidateUtil.validateField(description, "common.description", null, ConstantManager.ERR_EXIST_SPECIAL_CHAR_IN_SPECIAL);
				}
				// them vao
				if (StringUtil.isNullOrEmpty(errMsg)) {
					errMsg = ValidateUtil.validateField(noticeCode, "catalog.promotion.noticecode", 100, ConstantManager.ERR_REQUIRE, ConstantManager.ERR_EXIST_SPECIAL_CHAR_IN_SPECIAL, ConstantManager.ERR_MAX_LENGTH);
				}
				if (StringUtil.isNullOrEmpty(errMsg)) {
					errMsg = ValidateUtil.validateField(descriptionProduct, "catalog.promotion.descriptionproduct", 1000, ConstantManager.ERR_REQUIRE, ConstantManager.ERR_EXIST_SPECIAL_CHAR_IN_SPECIAL, ConstantManager.ERR_MAX_LENGTH);
				}
				//het
				if (!StringUtil.isNullOrEmpty(errMsg)) {
					result.put("errMsg", errMsg);
					result.put(ERROR, true);
					return SUCCESS;
				}
				PromotionProgram promotionProgram = promotionProgramMgr.getPromotionProgramByCode(promotionCode);
				if (promotionProgram != null) {
					result.put("errMsg", ValidateUtil.getErrorMsg(ConstantManager.ERR_EXIST, promotionCode));
					result.put(ERROR, true);
					return SUCCESS;
				}
				Date __startDate = null;
				Date __endDate = null;
				
				if (!StringUtil.isNullOrEmpty(startDate)) {
					__startDate = DateUtil.parse(startDate, DateUtil.DATE_FORMAT_DDMMYYYY);
				}
				if (!StringUtil.isNullOrEmpty(endDate)) {
					__endDate = DateUtil.parse(endDate, DateUtil.DATE_FORMAT_DDMMYYYY);
				}
				
				promotionProgram = new PromotionProgram();
				promotionProgram.setPromotionProgramCode(promotionCode);
				promotionProgram.setPromotionProgramName(promotionName);
				promotionProgram.setFromDate(__startDate);
				promotionProgram.setToDate(__endDate);
				promotionProgram.setStatus(ActiveType.WAITING);
				promotionProgram.setType(typeCode);
				ApParam apParam = apParamMgr.getApParamByCode(typeCode, ApParamType.PROMOTION);
				if (apParam != null) {
					promotionProgram.setProFormat(apParam.getValue());
				}
				promotionProgram.setIsEdited((!StringUtil.isNullOrEmpty(typeCode) && PromotionType.ZV26.getValue().equals(typeCode)) ? 1 : isEdited);
				noticeCode = noticeCode.trim();
				noticeCode = noticeCode.toUpperCase();
				promotionProgram.setNoticeCode(noticeCode);
				descriptionProduct = descriptionProduct.trim();
				promotionProgram.setDescriptionProduct(descriptionProduct);
				description = description.trim();
				promotionProgram.setDescription(description);
				if (discountType == null) {
					promotionProgram.setDiscountType(0);
					promotionProgram.setRewardType(rewardType);
				} else {
					promotionProgram.setDiscountType(discountType);
					promotionProgram.setRewardType(rewardType);
				}

				promotionProgram.setQuantiMonthNewOpen(quantiMonthNewOpen);
				if (Boolean.TRUE.equals(firstBuyFlag)) {
					promotionProgram.setFirstBuyFlag(PromotionProgramMgr.FIRST_BUY_CHECK);
					FirstBuyType type = null;
					if (firstBuyType != null) {
						type = FirstBuyType.parseValue(firstBuyType);
					}
					if (type != null) {
						promotionProgram.setFirstBuyType(type);
					} else {
						result.put("errMsg", Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "promotion.program.first.buy.type.no"));
						result.put(ERROR, true);
						return SUCCESS;
					}
					if (firstBuyNum != null && firstBuyNum > -1) {
						promotionProgram.setFirstBuyNum(firstBuyNum);
					} else {
						result.put("errMsg", Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "promotion.program.first.buy.num.no"));
						result.put(ERROR, true);
						return SUCCESS;
					}
				}
				if (Boolean.TRUE.equals(newCusFlag)) {
					promotionProgram.setNewCusFlag(PromotionProgramMgr.NEW_CUS_CHECK);
					if (newCusNumCycle != null && newCusNumCycle > -1) {
						promotionProgram.setNewCusNumCycle(newCusNumCycle);
					} else {
						result.put("errMsg", Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "promotion.program.new.cus.num.cycle.no"));
						result.put(ERROR, true);
						return SUCCESS;
					}
				}
				if (Boolean.FALSE.equals(ontopFlag) || PromotionType.ZV19.getValue().equals(typeCode) || PromotionType.ZV20.getValue().equals(typeCode) || PromotionType.ZV21.getValue().equals(typeCode)) {
					promotionProgram.setOntopFlag(null);
				} else {
					promotionProgram.setOntopFlag(PromotionProgramMgr.ONTOP_CHECK);
				}
				if (ActiveType.WAITING.equals(promotionProgram.getStatus())) {
					if (Boolean.TRUE.equals(haveRegulatedToStaffFlag)) {
						promotionProgram.setHaveRegulatedToStaff(PromotionProgramMgr.HAVE_REGULATED_TO_STAFF);
					} else {
						promotionProgram.setHaveRegulatedToStaff(PromotionProgramMgr.HAVE_REGULATED_TO_STAFF_NO);
					}
				}
				if (ActiveType.WAITING.equals(promotionProgram.getStatus())) {
					if (Boolean.TRUE.equals(haveRegulatedToCustFlag)) {
						promotionProgram.setHaveRegulatedToCust(PromotionProgramMgr.HAVE_REGULATED_TO_CUST);
					} else {
						promotionProgram.setHaveRegulatedToCust(PromotionProgramMgr.HAVE_REGULATED_TO_CUST_NO);
					}
				}
				
				//voucher = 2
				if(promotionProgram.getRewardType() != null && promotionProgram.getRewardType().equals(2)) {
					if(StringUtil.isNullOrEmpty(fromApplyDate)) {
						result.put("errMsg", Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "common.empty.date", R.getResource("common.date.fromapplydate")));
						result.put(ERROR, true);
						return SUCCESS;
					}
					if(DateUtil.checkInvalidFormatDate(fromApplyDate)) {
						result.put("errMsg", Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "common.invalid.format.date", R.getResource("common.date.fromapplydate")));
						result.put(ERROR, true);
						return SUCCESS;
					}
					if(DateUtil.compareDateWithoutTime(startApplyDate, sysDate) == -1) {
						result.put("errMsg", Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "common.date.greater.currentdate", R.getResource("common.date.fromapplydate")));
						result.put(ERROR, true);
						return SUCCESS;
					}							
					if(DateUtil.compareDateWithoutTime(startApplyDate, __startDate) == -1) {
						result.put("errMsg", Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "common.compare.error.less.or.equal.tow.param", R.getResource("common.date.fromdate"), R.getResource("common.date.fromapplydate")));
						result.put(ERROR, true);
						return SUCCESS;
					}
					promotionProgram.setFromApplyDate(startApplyDate);
					
					if(!StringUtil.isNullOrEmpty(toApplyDate) && DateUtil.checkInvalidFormatDate(toApplyDate)) {
						result.put("errMsg", Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "common.invalid.format.date", R.getResource("common.date.toapplydate")));
						result.put(ERROR, true);
						return SUCCESS;
					}
					if(endApplyDate != null && DateUtil.compareDateWithoutTime(endApplyDate, startApplyDate) == -1) {
						result.put("errMsg", Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "common.compare.error.less.or.equal.tow.param", R.getResource("common.date.fromapplydate"), R.getResource("common.date.toapplydate")));
						result.put(ERROR, true);
						return SUCCESS;
					}
					if(endApplyDate != null && DateUtil.compareDateWithoutTime(endApplyDate, sysDate) == -1) {
						result.put("errMsg", Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "common.date.greater.currentdate", R.getResource("common.date.toapplydate")));
						result.put(ERROR, true);
						return SUCCESS;
					}
					promotionProgram.setToApplyDate(endApplyDate);	
				}
				
				promotionProgram = promotionProgramMgr.createPromotionProgram(promotionProgram, getLogInfoVO());

				if (promotionProgram != null && promotionProgram.getId() > 0 && ActiveType.RUNNING.getValue().equals(promotionProgram.getNewCusFlag())) {
					PromotionNewcusConfig promotionNewcusConfig = new PromotionNewcusConfig();
					promotionNewcusConfig.setBrandIds(lstBrandId);
					promotionNewcusConfig.setCatIds(lstCategoryId);
					promotionNewcusConfig.setSubCatIds(lstSubCategoryId);
					promotionNewcusConfig.setPromotionProgram(promotionProgram);
					promotionNewcusConfigMgr.createPromotionNewcusConfig(promotionNewcusConfig, getLogInfoVO());
				}
				result.put(ERROR, false);
				result.put("promotionId", promotionProgram.getId());
			}
		} catch (Exception e) {
			LogUtility.logErrorStandard(e, R.getResource("web.log.message.error", "vnm.web.action.program.PromotionCatalogAction.update"), createLogErrorStandard(actionStartTime));
			result.put("errMsg", ValidateUtil.getErrorMsg(ConstantManager.ERR_SYSTEM));
			result.put(ERROR, true);
			return SUCCESS;
		} finally {
			lstCheckPromotionShopMap.clear();
			lstCheckPromotionShopMap = null;
			lstCheckPromotionShopJoin.clear();
			lstCheckPromotionShopJoin = null;
		}
		return SUCCESS;
	}
	
	public String importExcelNew() throws Exception {
		if(excelType.equals(3)) {			
			return importExcelPromotion24ZVNew();
		} else if(excelType.equals(2)) {		
			return importExcelPromotionNew();
		} else {
			return importExcel();
		}
	}
	
	/**
	 * import 24ZV new
	 * nghiep vụ mới
	 * */	
	public String importExcelPromotion24ZVNew() {
		try {
			List<List<String>> infoPromotion = new ArrayList<>();
			List<List<String>> infoPromotionDetail = new ArrayList<>();
			List<List<String>> infoPromotionShop = new ArrayList<>();
			
			List<CellBean> infoPromotionError = new ArrayList<>();
			List<CellBean> infoPromotionDetailError = new ArrayList<>();
			List<CellBean> infoPromotionShopError = new ArrayList<>();
			List<PromotionImportVO> promotionImportNewErrorVOs = null;
			
			getDataImportExcelPromotionNew(infoPromotion, infoPromotionDetail, infoPromotionShop, infoPromotionError, infoPromotionDetailError, infoPromotionShopError);
			// xu ly xuất lỗi
			if (infoPromotionError.size() > 0 || infoPromotionDetailError.size() > 0 || infoPromotionShopError.size() > 0) {
				return WriteFileErrorNew(infoPromotionError, infoPromotionDetailError, infoPromotionShopError);
			}			
			
			checkStructureFile(infoPromotionDetail, infoPromotionDetailError);
			if (infoPromotionError.size() > 0 || infoPromotionDetailError.size() > 0 || infoPromotionShopError.size() > 0) {
				return WriteFileErrorNew(infoPromotionError, infoPromotionDetailError, infoPromotionShopError);
			}
			
			promotionImportNewErrorVOs = new ArrayList<>();
			List<PromotionImportVO> promotionImportNewVOs = convertDataImportExcelPromotionNew(infoPromotion, infoPromotionDetail, infoPromotionShop, infoPromotionError, infoPromotionDetailError, infoPromotionShopError);
			if(promotionImportNewVOs != null && promotionImportNewVOs.size() > 0) {
				promotionImportNewVOs = validatePromotionImportNew(promotionImportNewVOs, promotionImportNewErrorVOs);
			}
			
			// sap xep lại cac mức cho CTKM
			promotionImportNewVOs = sortPromotionImportNew(promotionImportNewVOs);
			//save
			totalItem = promotionImportNewErrorVOs.size() + promotionImportNewVOs.size();
			numFail = promotionImportNewErrorVOs.size();
			if(promotionImportNewVOs != null && promotionImportNewVOs.size() > 0) {
				promotionImportNewErrorVOs = promotionProgramMgr.saveImportPromotionNewEx(promotionImportNewVOs, promotionImportNewErrorVOs, getLogInfoVO());
				// thông tin tra ve
				numFail = promotionImportNewErrorVOs.size();
				for (PromotionImportVO promotion : promotionImportNewVOs) {
					PromotionProgram pp = promotionProgramMgr.getPromotionProgramByCode(promotion.getPromotionCode());
					if (pp != null) {
						promotionProgramMgr.updateMD5ValidCode(pp, getLogInfoVO());
					}
				}
			}
			
			// xu ly nêu có loi
			if (promotionImportNewErrorVOs.size() > 0) {
				convertObjectPromotionToCellBeanNew(promotionImportNewErrorVOs, infoPromotionError, infoPromotionDetailError, infoPromotionShopError);
				if (infoPromotionError.size() > 0 || infoPromotionDetailError.size() > 0 || infoPromotionShopError.size() > 0) {
					return WriteFileErrorNew(infoPromotionError, infoPromotionDetailError, infoPromotionShopError);
				}
			}
		} catch(Exception ex) {
			errMsg = Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "system.error");
			LogUtility.logErrorStandard(ex, R.getResource("web.log.message.error", "vnm.web.action.program.PromotionCatalogAction.importExcelPromotionNew"), createLogErrorStandard(actionStartTime));
		}		
		return SUCCESS;
	}
	
	private void getDataImportExcelPromotionNew (List<List<String>> infoPromotion, List<List<String>> infoPromotionDetail, List<List<String>> infoPromotionShop, List<CellBean> infoPromotionError, List<CellBean> infoPromotionDetailError, List<CellBean> infoPromotionShopError) {
		InputStream is = null;
		Workbook promotionWorkBook = null;		
		List<String> promotionProgramCodes = new ArrayList<>();
		List<String> programAndTypeCodes = new ArrayList<>();
		
		try {
			is = new FileInputStream(excelFile);
			if (!is.markSupported()) {
				is = new PushbackInputStream(is, 8);
			}
			if (POIFSFileSystem.hasPOIFSHeader(is)) {
				promotionWorkBook = new HSSFWorkbook(is);
			} else if (POIXMLDocument.hasOOXMLHeader(is)) {
				promotionWorkBook = new XSSFWorkbook(OPCPackage.open(is));
			}
			if (promotionWorkBook != null) {
				Sheet promotionSheet = promotionWorkBook.getSheetAt(0);
				Sheet promotionDetailSheet = promotionWorkBook.getSheetAt(1);
				Sheet promotionShopSheet = promotionWorkBook.getSheetAt(2);
				if(promotionSheet!=null) {
					getPromotionInfoFromExcelFile(infoPromotion, promotionSheet, promotionProgramCodes, programAndTypeCodes, infoPromotionError);
				}
				if(promotionDetailSheet!=null) {
					getPromotionDetailFromExcelFile(infoPromotionDetail, promotionDetailSheet, promotionProgramCodes, programAndTypeCodes, infoPromotionDetailError);
				}
				if(promotionShopSheet!=null) {
					getPromotionShopMapFromExcelFile(infoPromotionShop, promotionShopSheet, promotionProgramCodes, programAndTypeCodes, infoPromotionShopError);
				}
			}
		} catch (FileNotFoundException e) {
			LogUtility.logError(e, "PromotionCatalogAction.getDataImportExcelPromotion()" + e.getMessage());
		} catch (InvalidFormatException e) {
			LogUtility.logError(e, "PromotionCatalogAction.getDataImportExcelPromotion()" + e.getMessage());
		} catch (IOException e) {
			LogUtility.logError(e, "PromotionCatalogAction.getDataImportExcelPromotion()" + e.getMessage());
		} catch (Exception e) {
			LogUtility.logError(e, "PromotionCatalogAction.getDataImportExcelPromotion()" + e.getMessage());
		}
	}
	
	private void getPromotionInfoFromExcelFile(List<List<String>> infoPromotion, Sheet sheetPromotionInfo, List<String> promotionCodes, List<String> programAndTypeCodes, List<CellBean> promotionInfoError) throws BusinessException {
		final int NUM_COL_PROMOTION_INFO_SHEET = 15;
		final List<String> FREE_ITEMS_REWARD_TYPES = Arrays.asList(PromotionType.ZV03.getValue(), PromotionType.ZV06.getValue(), PromotionType.ZV09.getValue()
				, PromotionType.ZV12.getValue(), PromotionType.ZV15.getValue(), PromotionType.ZV18.getValue(), PromotionType.ZV21.getValue(), PromotionType.ZV24.getValue());
		final List<String> PROGRAM_TYPE_SPECIALS = Arrays.asList(PromotionType.ZV19.getValue(), PromotionType.ZV20.getValue(), PromotionType.ZV22.getValue(), PromotionType.ZV23.getValue());
		boolean isContinue = true;
		String errMsg = "";
		Date fromDate = null;
		Date toDate = null;
		DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		dateFormat.setLenient(false);
		Iterator<?> rowIter = sheetPromotionInfo.rowIterator();
		rowIter.next();
		
		while (rowIter.hasNext()) {
			Row currentRow = (Row) rowIter.next();
			isContinue = true;
			for(int i = 0; i<NUM_COL_PROMOTION_INFO_SHEET; i++) {
				if (currentRow.getCell(i) != null && !StringUtil.isNullOrEmpty(getCellValueToString(currentRow.getCell(i)))) {
					isContinue = false;
					break;
				}
			}
			
			if(isContinue) {
				continue;
			}
			
			List<String> rowData = new ArrayList<>();
			//MA CHUONG TRINH KHUYEN MAI
			rowData.add(getCellValueToString(currentRow.getCell(0)).toUpperCase().trim());
			//TEN CHUONG TRINH KHUYEN MAI
			rowData.add(getCellValueToString(currentRow.getCell(1)).trim());
			//PHIEN BAN
			rowData.add(getCellValueToString(currentRow.getCell(2)).trim());
			//LOAI CHUONG TRINH KHUYEN MAI
			rowData.add(getCellValueToString(currentRow.getCell(3)).trim());
			//TU NGAY
			rowData.add(getCellValueToString(currentRow.getCell(4)).trim());
			//DEN NGAY
			rowData.add(getCellValueToString(currentRow.getCell(5)).trim());
			//SO THONG BAO
			rowData.add(getCellValueToString(currentRow.getCell(6)).trim());
			//TEN NHOM / SAN PHAM
			rowData.add(getCellValueToString(currentRow.getCell(7)).trim());
			//MO TA CHUONG TRINH 
			rowData.add(getCellValueToString(currentRow.getCell(8)).trim());
			//BOI SO
			rowData.add(getCellValueToString(currentRow.getCell(9)).trim());
			//TOI UU
			rowData.add(getCellValueToString(currentRow.getCell(10)).trim());
			//LOAI TRA THUONG
			rowData.add(getCellValueToString(currentRow.getCell(11)).trim());
			//TU NGAY TRA THUONG
			rowData.add(getCellValueToString(currentRow.getCell(12)).trim());
			//DEN NGAY TRA THUONG
			rowData.add(getCellValueToString(currentRow.getCell(13)).trim());
			//LOAI CHIEU KHAU
			rowData.add(getCellValueToString(currentRow.getCell(14)).trim());
			errMsg = "";
			//Kiem tra Ma Chuong trinh
			String promotionCode  = rowData.get(0);
			boolean isDuplicated = false;
			if (StringUtil.isNullOrEmpty(promotionCode)) {
				errMsg += R.getResource("catalog.promotion.import.column.null", "Mã CTKM");
			} else {
				errMsg += ValidateUtil.validateField(promotionCode, "catalog.promotion.import.column.progcode", 50, ConstantManager.ERR_REQUIRE, ConstantManager.ERR_MAX_LENGTH, ConstantManager.ERR_EXIST_SPECIAL_CHAR_IN_CODE);
			}
			for (int i = 0; i < infoPromotion.size(); i++) {
				if (promotionCode.equals(infoPromotion.get(i).get(0))) {
					isDuplicated = true;
					break;
				}
			}
			
			if (isDuplicated) {
				errMsg += Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "promotion.product.import.duplicate", rowData);
				errMsg += "\n";
			}
			
			PromotionProgram existPromotion = null;
			existPromotion = promotionProgramMgr.getPromotionProgramByCode(promotionCode);
			if (existPromotion != null && !ActiveType.WAITING.equals(existPromotion.getStatus())) {
				errMsg += Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "catalog.promotion.program.is.running");
				errMsg += "\n";
			}
			//Kiem tra ten chuong trinh
			String promotionName  = rowData.get(1);
			if (promotionName.isEmpty()) {
				errMsg += R.getResource("catalog.promotion.import.column.null", "Tên CTKM");
			} else {
				if(promotionName.length() > 500) {
					errMsg += R.getResource("catalog.promotion.import.over.max.length") + "\n";
					errMsg = errMsg.replaceAll("%max%", "500");
					errMsg = errMsg.replaceAll("%colName%", "Tên CTKM");
				} else {
					errMsg += ValidateUtil.validateField(promotionName, "catalog.promotion.name", 500, ConstantManager.ERR_EXIST_SPECIAL_CHAR_IN_NAME);	
				}
			}
			//Kiem tra loai Chuong trinh
			String typePromotion = rowData.get(3).toUpperCase();
			if(typePromotion.isEmpty()) {
				errMsg += Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "common.required", Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "catalog.promotion.type"));
				errMsg += "\n";
			} else {
				if(!PROMOTION_TYPES.contains(typePromotion)) {
					errMsg += Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "catalog.exists.before", Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "catalog.promotion.type"));
					errMsg += "\n";
				}
			}
			fromDate = null;
			Date promotionBeginDate = null;
			//kiem tra TU NGAY
			String fromDatePromo = rowData.get(4);
			if(fromDatePromo.isEmpty()) {
				errMsg += Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "common.required", Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "imp.epx.tuyen.clmn.tuNgay"));
				errMsg += "\n";
			} else {
				try {
					fromDate = dateFormat.parse(fromDatePromo);
					promotionBeginDate = fromDate;
					if(fromDate!=null&&fromDate.before(dateFormat.parse(dateFormat.format(new Date())))) {
						errMsg += R.getResource("common.invalid.format.date.before", R.getResource("imp.epx.tuyen.clmn.tuNgay"),R.getResource("imp.epx.tuyen.clmn.currentDate"));
						errMsg += "\n";
					}
				} catch (ParseException e) {
					errMsg += R.getResource("common.invalid.format.date", R.getResource("imp.epx.tuyen.clmn.tuNgay"));
					errMsg += "\n";
				}
			}
			
			//kiem tra DEN NGAY
			String toDatePromo = rowData.get(5);
			if(!toDatePromo.isEmpty()) {
				try {
					toDate = dateFormat.parse(toDatePromo);
					if(fromDate!=null && fromDate.after(toDate)) {
						errMsg += R.getResource("common.fromdate.greater.todate") + "\n";
						errMsg += "\n";
					}
				} catch (ParseException e) {
					errMsg += R.getResource("common.invalid.format.date", R.getResource("imp.epx.tuyen.clmn.denNgay"));
					errMsg += "\n";
				}
			}
			
			//kiem tra so thong bao
			String noticeCode = rowData.get(6).toUpperCase();
			if(!noticeCode.isEmpty()){
				if(noticeCode.length() > 100) {
					errMsg += R.getResource("catalog.promotion.import.over.max.length") + "\n";
					errMsg = errMsg.replaceAll("%max%", "100");
					errMsg = errMsg.replaceAll("%colName%", "Số thông báo");
				} else {
					errMsg += ValidateUtil.validateField(noticeCode, "catalog.promotion.noticecode", 100, ConstantManager.ERR_EXIST_SPECIAL_CHAR_IN_SPECIAL);	
				}
			} else {
				errMsg += R.getResource("catalog.promotion.import.notice.code.obligate") + "\n";
			}
			 
			//kiem tra ten / nhom san pham
			String groupProductName = rowData.get(7);			
			if (!groupProductName.isEmpty()) {
				if(groupProductName.length()>1000) {
					errMsg += R.getResource("catalog.promotion.import.over.max.length") + "\n";
					errMsg = errMsg.replaceAll("%max%", "1000");
					errMsg = errMsg.replaceAll("%colName%", "Nhóm/Tên SP hàng bán");
				} else {
					errMsg += ValidateUtil.validateField(groupProductName, "catalog.promotion.descriptionproduct", 1000, ConstantManager.ERR_EXIST_SPECIAL_CHAR_IN_SPECIAL);
				}
			} else {
				errMsg += R.getResource("catalog.promotion.import.description.product.obligate") + "\n";
			}
			
			// kiem tra mo ta chuong trinh
			String desPromotion = rowData.get(8);
			if(desPromotion.length()>1000) {
				errMsg += R.getResource("catalog.promotion.import.over.max.length") + "\n";
				errMsg = errMsg.replaceAll("%max%", "1000");
				errMsg = errMsg.replaceAll("%colName%", "Mô tả chương trình");
			}
			// kiem tra boi so
			String multiply = rowData.get(9);
			if(!multiply.isEmpty())
			if(!("0".equals(multiply) || "1".equals(multiply))) {
				errMsg += Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "catalog.promotion.import.multiple.incorrect.format") + "\n";
			}
			String 	rescursive = rowData.get(10);
			// kiem tra toi uu
			if(!rescursive.isEmpty())
			if(!("0".equals(rescursive) || "1".equals(rescursive))) {
				errMsg += Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "catalog.promotion.import.recursive.incorrect.format") + "\n";
			}
			//Kiem tra LOAI TRA THUONG
			String paymentType = rowData.get(11) ;
			if(!("2".equals(paymentType) || "1".equals(paymentType) || (FREE_ITEMS_REWARD_TYPES.contains(typePromotion)&&paymentType.isEmpty()))) {
				errMsg += R.getResource("ctkm.import.new.product.rewardtype");
				errMsg += "\n";
			} else {
				if(FREE_ITEMS_REWARD_TYPES.contains(typePromotion)&&"2".equals(paymentType)) {
					errMsg += R.getResource("catalog.promotion.import.voucher.not.use");
					errMsg += "\n";
				}
			}

			fromDate = null;
			//kiem tra TU NGAY TRA THUONG
			String fromDateReturn = rowData.get(12);
			if(!fromDateReturn.isEmpty()) {
				if(!"2".equals(paymentType)) {
					errMsg += R.getResource("catalog.promotion.import.reward.date.not.use");
					errMsg += "\n";
				} else {
					try {
						fromDate = dateFormat.parse(fromDateReturn);
						if(fromDate!=null&&fromDate.before(dateFormat.parse(dateFormat.format(new Date())))) {
							errMsg += R.getResource("common.invalid.format.date.before", R.getResource("imp.epx.tuyen.clmn.tuNgay.traThuong"),R.getResource("imp.epx.tuyen.clmn.currentDate"));
							errMsg += "\n";
						}
					} catch (ParseException e) {
						errMsg += R.getResource("common.invalid.format.date", R.getResource("imp.epx.tuyen.clmn.tuNgay.traThuong"));
						errMsg += "\n";
					}
				}

				if(promotionBeginDate!=null&&fromDate!=null&&fromDate.before(promotionBeginDate)) {
					errMsg += R.getResource("common.invalid.format.date.before", R.getResource("imp.epx.tuyen.clmn.tuNgay.traThuong"),R.getResource("imp.epx.tuyen.clmn.tuNgay"));
					errMsg += "\n";
				}
			}
			
			//kiem tra DEN NGAY TRA THUONG
			String toDateReturn = rowData.get(13);
			if(!toDateReturn.isEmpty()) {
				try {
					toDate = dateFormat.parse(toDateReturn);
					if(fromDate!=null && fromDate.after(toDate)) {
						errMsg += R.getResource("common.fromdate.greater.todate.reward") + "\n";
						errMsg += "\n";
					}
				} catch (ParseException e) {
					errMsg += R.getResource("common.invalid.format.date", R.getResource("imp.epx.tuyen.clmn.denNgay.traThuong"));
					errMsg += "\n";
				}
			}
			
			String typeDiscount = rowData.get(14);
			if(PROGRAM_TYPE_SPECIALS.contains(rowData.get(3))) {
					if(!"1".equals(typeDiscount) && !"2".equals(typeDiscount)) {
						errMsg += R.getResource("ctkm.import.new.product.discountType");
						errMsg += "\n";
					}
			}
			
			if(!StringUtil.isNullOrEmpty(errMsg)) {
				CellBean cb = new CellBean();
				cb.setContent1(rowData.get(0));
				cb.setContent2(rowData.get(1));
				cb.setContent3(rowData.get(2));
				cb.setContent4(rowData.get(3));
				cb.setContent5(rowData.get(4));
				cb.setContent6(rowData.get(5));
				cb.setContent7(rowData.get(6));
				cb.setContent8(rowData.get(7));
				cb.setContent9(rowData.get(8));
				cb.setContent10(rowData.get(9));
				cb.setContent11(rowData.get(10));
				cb.setContent12(rowData.get(11));	
				cb.setContent13(rowData.get(12));
				cb.setContent14(rowData.get(13));
				cb.setContent15(rowData.get(14));
				cb.setErrMsg(errMsg);
				promotionInfoError.add(cb);
			}
			
			if(!promotionCodes.contains(promotionCode)) {
				promotionCodes.add(promotionCode);
			}
			String programAndTypeCode = promotionCode + typePromotion;
			if(!programAndTypeCodes.contains(programAndTypeCode)) {
				programAndTypeCodes.add(programAndTypeCode);
			}
			infoPromotion.add(rowData);	
		}
	}
	
	private void getPromotionDetailFromExcelFile(List<List<String>> infoPromotionDetail, Sheet sheetPromotionDetail, List<String> promotionCodes, List<String> programAndTypeCodes, List<CellBean> promotionDetailError) throws BusinessException {
		final int NUM_COL_PROMOTION_DETAIL_SHEET = 17;
		boolean isContinue = true;
		String errMsg = "";
		Iterator<?> rowIter = sheetPromotionDetail.rowIterator();
		rowIter.next();
		while (rowIter.hasNext()) {
			Row currentRow = (Row) rowIter.next();
			isContinue = true;
			for(int i = 0; i<NUM_COL_PROMOTION_DETAIL_SHEET; i++) {
				if (currentRow.getCell(i) != null && !StringUtil.isNullOrEmpty(getCellValueToString(currentRow.getCell(i)))) {
					isContinue = false;
					break;
				}
			}
			
			if(isContinue) {
				continue;
			}
			List<String> rowData = new ArrayList<>();
			//MA CHUONG TRINH KHUYEN MAI
			rowData.add(getCellValueToString(currentRow.getCell(0)).trim());
			//LOAI CHUONG TRINH KHUYEN MAI
			rowData.add(getCellValueToString(currentRow.getCell(1)).trim());
			//MA NHOM
			rowData.add(getCellValueToString(currentRow.getCell(2)).trim());
			//MA MUC
			rowData.add(getCellValueToString(currentRow.getCell(3)).trim());
			//MUC CHA
			rowData.add(getCellValueToString(currentRow.getCell(4)).trim());
			//MUC CON
			rowData.add(getCellValueToString(currentRow.getCell(5)).trim());
			//MA SAN PHAM MUA
			rowData.add(getCellValueToString(currentRow.getCell(6)).trim());
			//SO LUONG SAN PHAM MUA
			rowData.add(getCellValueToString(currentRow.getCell(7)).trim());
			//DON VI TINH CHO SP MUA
			rowData.add(getCellValueToString(currentRow.getCell(8)).trim());
			//SO TIEN SP MUA
			rowData.add(getCellValueToString(currentRow.getCell(9)).trim());
			//THUOC TINH BAT BUOC CHO SP MUA
			rowData.add(getCellValueToString(currentRow.getCell(10)).trim());
			//SO TIEN SP KM
			rowData.add(getCellValueToString(currentRow.getCell(11)).trim());
			//% KM
			rowData.add(getCellValueToString(currentRow.getCell(12)).trim());
			//MA SP KM
			rowData.add(getCellValueToString(currentRow.getCell(13)).trim());
			//SO LUONG KM
			rowData.add(getCellValueToString(currentRow.getCell(14)).trim());
			//DON VI TINH CHO SPKM
			rowData.add(getCellValueToString(currentRow.getCell(15)).trim());
			//THUOC TINH BAT BUOC
			rowData.add(getCellValueToString(currentRow.getCell(16)).trim());
			
			errMsg = "";
			// KIEM TRA MA CHUONG TRINH
			String promotionCode = rowData.get(0).toUpperCase();
			if(!promotionCode.isEmpty()) {
				errMsg += ValidateUtil.validateField(promotionCode, "catalog.promotion.import.column.progcode", 50, ConstantManager.ERR_REQUIRE, ConstantManager.ERR_MAX_LENGTH, ConstantManager.ERR_EXIST_SPECIAL_CHAR_IN_CODE);
				PromotionProgram existPromotion = null;
				existPromotion = promotionProgramMgr.getPromotionProgramByCode(promotionCode);
				if (existPromotion != null && !ActiveType.WAITING.equals(existPromotion.getStatus())&&errMsg.isEmpty()) {
					errMsg += Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "catalog.promotion.program.is.running");
					errMsg += "\n";
				}
				if(!promotionCodes.contains(promotionCode)) {
					errMsg += R.getResource("catalog.promotion.import.not.init") + "\n";
				}				
			} else {
				errMsg += Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "common.required", Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "catalog.promotion.code"));
				errMsg += "\n";
			}
			//KIEM TRA LOAI CHUONG TRINH
			String typePromotion = rowData.get(1).toUpperCase();
			if(typePromotion.isEmpty()) {
				errMsg += Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "common.required", Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "catalog.promotion.type"));
				errMsg += "\n";
			} else {
				if(!PROMOTION_TYPES.contains(typePromotion)) {
					errMsg += Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "catalog.exists.before", Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "catalog.promotion.type"));
					errMsg += "\n";
				}
				if(!programAndTypeCodes.contains(promotionCode+typePromotion)) {
					errMsg += Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "catalog.promotion.import.type.is.not.same2") + "\n";
				}
			}
			//KIEM TRA MA NHOM
			String groupCode = rowData.get(2).toUpperCase();
			if(groupCode.isEmpty()) {
				errMsg += Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "catalog.promotion.import.promotion.group.code.obligate");
				errMsg += "\n";
			} else {
				errMsg += ValidateUtil.validateField(groupCode, "catalog.promotion.import.column.groupcode", 50, ConstantManager.ERR_REQUIRE, 
						 ConstantManager.ERR_MAX_LENGTH, ConstantManager.ERR_EXIST_SPECIAL_CHAR_IN_CODE);
			}
			//KIEM TRA MA MUC
			String levelCode = rowData.get(3).toUpperCase();
			if(levelCode.isEmpty()) {
				errMsg += Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "catalog.promotion.import.promotion.level.code.obligate");
				errMsg += "\n";
			} else {
				errMsg += ValidateUtil.validateField(levelCode, "catalog.promotion.import.column.levelcode", 50, ConstantManager.ERR_REQUIRE, 
						 ConstantManager.ERR_MAX_LENGTH, ConstantManager.ERR_EXIST_SPECIAL_CHAR_IN_CODE);
			}
			//KIEM TRA MUC CHA
			String parentLevel =  rowData.get(4).toUpperCase();
			if(!parentLevel.isEmpty()) {
				if(!"X".equals(parentLevel)) {
					errMsg+= R.getResource("catalog.promotion.import.selected.invalid",R.getResource("catalog.promotion.import.khtg.ctkm.clmn.parent"));
				}
			}
			
			//KIEM TRA MUC CON
			String childLevel =  rowData.get(5).toUpperCase();
			if(!childLevel.isEmpty()) {
				if(!"X".equals(childLevel)) {
					errMsg+= R.getResource("catalog.promotion.import.selected.invalid",R.getResource("catalog.promotion.import.khtg.ctkm.clmn.child"));
				}
			}
			if(!parentLevel.isEmpty()&&!childLevel.isEmpty()) {
				errMsg+= Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "catalog.promotion.import.parent.and.child");
			}
			//KIEM TRA MA SAN PHAM
			String productCode = rowData.get(6).toUpperCase();
			if(!productCode.isEmpty()) {
				Product product = productMgr.getProductByCode(productCode.trim());
				if (product == null) {
					errMsg += Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "common.not.exist.in.db", Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "catalog.buyproduct.code"));
					errMsg += "\n";
				} else {
					if(!ActiveType.RUNNING.equals(product.getStatus())) {
						errMsg += Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "ctkm.import.new.product.inactive", productCode);
						errMsg += "\n";
					}
				}
			}
			//KIEM TRA SO LUONG SP
			String productQuantity = rowData.get(7);
			if(!productQuantity.isEmpty()) {
				try {
					Integer quantity = Integer.parseInt(productQuantity);
					if(quantity<=0) {
						errMsg += Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "catalog.promotion.import.column.invalid.format.number", "SL Sản Phẩm Mua");
					}
				} catch (NumberFormatException e) {
					errMsg += R.getResource("catalog.promotion.import.column.invalid.format.number", "SL Sản Phẩm Mua");
				}

			}
			//KIEM TRA DON VI TINH CHO SP MUA
			String unitProduct = rowData.get(8).toUpperCase();
			if(!unitProduct.isEmpty()) {
				if(!UNIT.contains(unitProduct)) {
					errMsg+= Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "common.not.date", "Đơn Vị Tính cho SP Mua");
				}
			} 
			//KIEM TRA SO TIEN SP MUA
			String amountProduct = rowData.get(9);
			if(!amountProduct.isEmpty())
			try {
				Double amount = Double.parseDouble(amountProduct);
				if(amount<=0) {
					errMsg += Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "catalog.promotion.import.column.invalid.format.number", "Số Tiền SP Mua");
				}
			} catch (NumberFormatException e) {
				errMsg += R.getResource("catalog.promotion.import.column.invalid.format.number", "Số Tiền SP Mua");
			}
			//Kiem tra THUOC TINH BAT BUOC CHO SP MUA
			String productCondition =  rowData.get(10).toUpperCase();
			if(!productCondition.isEmpty()) {
				if(!"X".equals(productCondition)) {
					errMsg+= R.getResource("catalog.promotion.import.selected.invalid",R.getResource("catalog.promotion.import.khtg.ctkm.clmn.buy.required"));
				}
			}
			//KIEM TRA SO TIEN SP KM
			String discountAmount = rowData.get(11);
			if(!discountAmount.isEmpty())
			try {
				Double discount = Double.parseDouble(discountAmount);
				if(discount<=0) {
					errMsg += Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "catalog.promotion.import.column.invalid.format.number", "Số Tiền SP KM");
				}
			} catch (NumberFormatException e) {
				errMsg += R.getResource("catalog.promotion.import.column.invalid.format.number", "Số Tiền SP KM");
			}
			//KIEM TRA % khuyen mai
			String percentPromo = rowData.get(12);
			if(!percentPromo.isEmpty())
			try {
				Double percent = Double.parseDouble(percentPromo);
				if(percent<= (double) 0 || percent > (double) 100) {
					errMsg += Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "ctkm.import.new.percent.zero");
				}
			} catch (NumberFormatException e) {
				errMsg += R.getResource("catalog.promotion.import.column.invalid.format.number", "% KM");
			}
			//KIEM TRA MA SP Khuyen MAI
			String promoProductCode = rowData.get(13).toUpperCase();
			if(!promoProductCode.isEmpty()) {
				Product promoProduct = productMgr.getProductByCode(promoProductCode);
				if (promoProduct == null) {
					errMsg += Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "common.not.exist.in.db", Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "catalog.disproduct.code"));
					errMsg += "\n";
				} else {
					if(!ActiveType.RUNNING.equals(promoProduct.getStatus())) {
						errMsg += Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "ctkm.import.new.product.inactive", promoProductCode);
						errMsg += "\n";
					}
				}
			}
			//KIEM TRA SO LUONG KM
			String promoQuantity = rowData.get(14);
			if(!promoQuantity.isEmpty())
			try {
				Integer quantity = Integer.parseInt(promoQuantity);
				if(quantity<=0) {
					errMsg += Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "catalog.promotion.import.column.invalid.format.number", "Số Lượng KM");
				}
			} catch (NumberFormatException e) {
				errMsg += R.getResource("catalog.promotion.import.column.invalid.format.number", "Số Lượng KM");
			}
			//Kiem tra DON VI TINH CHO SP KHUYEN MAI
			String unitPromoProduct = rowData.get(15).toUpperCase();
			if(!unitPromoProduct.isEmpty()) {
				if(!UNIT.contains(unitPromoProduct)) {
					errMsg+= Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "common.not.date", "Đơn Vị Tính cho SP KM");
				}
			}
			//Kiem tra THUOC TINH BAT BUOC
			String promoCondition =  rowData.get(16).toUpperCase();
			if(!promoCondition.isEmpty()) {
				if(!"X".equals(promoCondition)) {
					errMsg+= R.getResource("catalog.promotion.import.selected.invalid",R.getResource("catalog.promotion.import.khtg.ctkm.clmn.KM.required"));
				}
			}
			//Kiem tra du lieu muc
			if(childLevel.isEmpty()&&parentLevel.isEmpty()&&promoProductCode.isEmpty()&&productCode.isEmpty()) {
				errMsg+= R.getResource("catalog.promotion.import.required.product");
			}
			if(!errMsg.isEmpty()) {
				CellBean cb = new CellBean();
				cb.setContent1(rowData.get(0));
				cb.setContent2(rowData.get(1));
				cb.setContent3(rowData.get(2));
				cb.setContent4(rowData.get(3));
				cb.setContent5(rowData.get(4));
				cb.setContent6(rowData.get(5));
				cb.setContent7(rowData.get(6));
				cb.setContent8(rowData.get(7));
				cb.setContent9(rowData.get(8));
				cb.setContent10(rowData.get(9));
				cb.setContent11(rowData.get(10));
				cb.setContent12(rowData.get(11));    
				cb.setContent13(rowData.get(12));
				cb.setContent14(rowData.get(13));
				cb.setContent15(rowData.get(14));
				cb.setContent16(rowData.get(15));
				cb.setContent17(rowData.get(16));
				cb.setErrMsg(errMsg);
				promotionDetailError.add(cb);
			}
			infoPromotionDetail.add(rowData);
		}
	}
	
	private void getPromotionShopMapFromExcelFile(List<List<String>> infoPromotionShop, Sheet sheetPromotionShopMap, List<String> promotionCodes, List<String> programAndTypeCodes, List<CellBean> promotionShopMapError) throws BusinessException, DataAccessException {	
		final int NUM_COL_PROMOTION_SHOP_MAP_SHEET = 5;
		boolean isContinue = true;
		String errMsg = "";
		Iterator<?> rowIter = sheetPromotionShopMap.rowIterator();
		rowIter.next();
		while (rowIter.hasNext()) {
			Row currentRow = (Row) rowIter.next();
			isContinue = true;
			for(int i = 0; i<NUM_COL_PROMOTION_SHOP_MAP_SHEET; i++) {
				if (currentRow.getCell(i) != null && !StringUtil.isNullOrEmpty(getCellValueToString(currentRow.getCell(i)))) {
					isContinue = false;
					break;
				}
			}
			
			if(isContinue) {
				continue;
			}
			
			List<String> rowData = new ArrayList<>();
			//MA CHUONG TRINH
			rowData.add(getCellValueToString(currentRow.getCell(0)).trim());
			//MA DON VI
			rowData.add(getCellValueToString(currentRow.getCell(1)).trim());
			//SO SUAT
			rowData.add(getCellValueToString(currentRow.getCell(2)).trim());
			//SO TIEN
			rowData.add(getCellValueToString(currentRow.getCell(3)).trim());
			//SO LUONG
			rowData.add(getCellValueToString(currentRow.getCell(4)).trim());
			
			//Kiem tra
			errMsg = "";
			//kiem tra ma chuong trinh
			String promoCode = rowData.get(0);
			if (promoCode.isEmpty()) {
				errMsg += R.getResource("catalog.promotion.import.promotion.code.obligate") + "\n";
			} else {
				if(!promotionCodes.contains(promoCode.toUpperCase())) {
					errMsg += R.getResource("catalog.promotion.import.not.init") + "\n";
				}
			}
			//Kiem tra MA DON VI
			String shopCode = rowData.get(1).toUpperCase();
			if(shopCode.isEmpty()) {
				errMsg += R.getResource("catalog.promotion.import.unit.code.obligate") + "\n";
			} else {
				if(shopMgr.getShopByCode(shopCode) == null){
					errMsg += R.getResource("catalog.promotion.import.unit.code.not.permission") + "\n";
				} else if (currentUser != null && currentUser.getShopRoot() != null){ 
					// kiem tra don vi co thuoc quyen quan ly cua user
					List<Shop> listShopChild = promotionProgramMgr.getListChildByShopId(currentUser.getShopRoot().getShopId());
					// Kiem tra shop co thuoc quen quan ly cua user dang nhap
					boolean isShopMapWithUser = false;
					for(Shop shop: listShopChild){
						if(shopCode.toLowerCase().equals(shop.getShopCode().toLowerCase())){
							isShopMapWithUser = true;
							break;
						}
					}
					if(!isShopMapWithUser){
						errMsg += R.getResource("catalog.promotion.import.unit.code.not.permission.by.current.user") + "\n";
					}
				}
				
				// kiem tra SO SUAT
				String quantityMax = rowData.get(2);
				if(!quantityMax.isEmpty()){
					if(quantityMax.length() > 9 ) {
						errMsg += R.getResource("catalog.promotion.import.over.max.length") + "\n";
						errMsg = errMsg.replaceAll("%max%", "9");
						errMsg = errMsg.replaceAll("%colName%", "Số suất");
					}
					try { 
						Integer num = Integer.parseInt(quantityMax);
						if(num<0) {
							errMsg += Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "catalog.promotion.import.quantity.max.incorrect.format");
						}
					} catch(NumberFormatException e) {
						errMsg += R.getResource("catalog.promotion.import.quantity.max.incorrect.format") + "\n";
					}
				}
				// kiem tra SO TIEN
				String amountMax = rowData.get(3);
				if(!amountMax.isEmpty()){
					if(amountMax.length() > 9 ){
						errMsg += R.getResource("catalog.promotion.import.over.max.length") + "\n";
						errMsg = errMsg.replaceAll("%max%", "9");
						errMsg = errMsg.replaceAll("%colName%", "Số tiền");
					}
					try { 
						Double num = Double.parseDouble(amountMax);
						if(num<0) {
							errMsg += Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "catalog.promotion.import.amount.max.incorrect.format");
						}
					} catch(NumberFormatException e) {
						errMsg += R.getResource("catalog.promotion.import.amount.max.incorrect.format") + "\n";
					}
				}
				//kiemn tra SO LUONG
				String numMax = rowData.get(4);
				if (!numMax.isEmpty()) {
					if(numMax != null && numMax.length() > 9){
						errMsg += R.getResource("catalog.promotion.import.over.max.length") + "\n";
						errMsg = errMsg.replaceAll("%max%", "9");
						errMsg = errMsg.replaceAll("%colName%", "Số lượng");
					}
					try { 
						Integer num = Integer.parseInt(amountMax);
						if(num<0) {
							errMsg += Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "catalog.promotion.import.num.max.incorrect.format");
						}
					} catch(NumberFormatException e) {
						errMsg += R.getResource("catalog.promotion.import.num.max.incorrect.format") + "\n";
					}
				}
			}
			if(!errMsg.isEmpty()) {
				CellBean cb = new CellBean();
				cb.setContent1(rowData.get(0));
				cb.setContent2(rowData.get(1));
				cb.setContent3(rowData.get(2));
				cb.setContent4(rowData.get(3));
				cb.setContent5(rowData.get(4));
				cb.setErrMsg(errMsg);
				promotionShopMapError.add(cb);
			}
			infoPromotionShop.add(rowData);
		}
	}
	
	private void checkStructureFile (List<List<String>> infoPromotionDetail, List<CellBean> infoPromotionDetailError) {
		int rowIndex = 0;
		int len = infoPromotionDetail.size();
		List<String> parentHeader;
		List<String> row;
		String parentHeaderText;
		String rowText;
		boolean extraRowLevel;
		boolean childLevel;
		while(rowIndex < len) {	
			parentHeader = infoPromotionDetail.get(rowIndex);
			parentHeaderText = parentHeader.get(0)+parentHeader.get(2)+parentHeader.get(3);
			if(!isParentHeader(parentHeader)) {
				infoPromotionDetailError.add(convertDetailRowToCellBean(parentHeader,Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "ctkm.import.new.structure", rowIndex+2)));
				return;
			}
			rowIndex++;
			extraRowLevel = false;
			childLevel = false;
			while(rowIndex < len) {
				row = infoPromotionDetail.get(rowIndex);
				rowText = row.get(0)+row.get(2)+row.get(3);
				if(!rowText.equalsIgnoreCase(parentHeaderText)) {
					break;
				}
				if(isParentHeader(row)) {
					infoPromotionDetailError.add(convertDetailRowToCellBean(row,Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "ctkm.import.new.structure", rowIndex+2)));
					return;
				}
				if(isParentContent(row)) {
					if(!extraRowLevel&&!childLevel) {
						rowIndex++;
						continue;
					} else {
						infoPromotionDetailError.add(convertDetailRowToCellBean(row,Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "ctkm.import.new.structure", rowIndex+2)));
						return;
					}
				}
				if(isExtraRow(row)) {
					extraRowLevel = true;
					rowIndex++;
					continue;
				}
				if(!isChildHeader(row)) {
					infoPromotionDetailError.add(convertDetailRowToCellBean(row,Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "ctkm.import.new.structure", rowIndex+2)));
					return;
				} else {
					if(extraRowLevel)  {
						infoPromotionDetailError.add(convertDetailRowToCellBean(row,Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "ctkm.import.new.structure", rowIndex+2)));
						return;
					}
					childLevel = true;					
				}
				boolean hasChild = false;
				rowIndex++;
				while(rowIndex<len) {
					row = infoPromotionDetail.get(rowIndex);
					if(isChildContent(row)) {
						hasChild = true;
					} else {
						break;
					}
					rowIndex++;
				}
				if(!hasChild) {
					infoPromotionDetailError.add(convertDetailRowToCellBean(row,Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "ctkm.import.new.structure", rowIndex+1)));
					return;
				}				
			}
		}
	}
	
	private boolean isParentHeader(List<String> row) {
		//Kiem tra header cua Muc cha: la muc cha, khong phai muc con, khong co san pham
		if(!row.get(4).isEmpty()&&row.get(5).isEmpty()&&row.get(6).isEmpty()) {
			return true;
		}
		return false;
	}
	
	private boolean isParentContent(List<String> row) {
		//Kiem tra header cua Muc cha: la muc cha, khong phai muc con, co san pham
		if(!row.get(4).isEmpty()&&row.get(5).isEmpty()&&!row.get(6).isEmpty()) {
			return true;
		}
		return false;
	}
	
	private boolean isChildHeader(List<String> row) {
		//Kiem tra header cua Muc cha: khong phai muc cha,la muc con, khong co san pham
		if(row.get(4).isEmpty()&&!row.get(5).isEmpty()&&row.get(6).isEmpty()) {
			return true;
		}
		return false;
	}
	
	private boolean isChildContent(List<String> row) {
		//Kiem tra header cua Muc cha: khong phai cha, khong phai con, khong co san pham
		if(row.get(4).isEmpty()&&!row.get(5).isEmpty()&&!row.get(6).isEmpty()) {
			return true;
		}
		return false;
	}
	
	private boolean isExtraRow(List<String> row) {
		//Kiem tra header cua Muc cha: khong phai cha, khong phai con, khong co san pham
		if(row.get(4).isEmpty()&&row.get(5).isEmpty()&&row.get(6).isEmpty()) {
			return true;
		}
		return false;
	}
	
	private CellBean convertDetailRowToCellBean(List<String> rowData,String errMsg) {
		CellBean cb =  new CellBean();
		cb.setContent1(rowData.get(0));
		cb.setContent2(rowData.get(1));
		cb.setContent3(rowData.get(2));
		cb.setContent4(rowData.get(3));
		cb.setContent5(rowData.get(4));
		cb.setContent6(rowData.get(5));
		cb.setContent7(rowData.get(6));
		cb.setContent8(rowData.get(7));
		cb.setContent9(rowData.get(8));
		cb.setContent10(rowData.get(9));
		cb.setContent11(rowData.get(10));
		cb.setContent12(rowData.get(11));    
		cb.setContent13(rowData.get(12));
		cb.setContent14(rowData.get(13));
		cb.setContent15(rowData.get(14));
		cb.setContent16(rowData.get(15));
		cb.setContent17(rowData.get(16));
		cb.setErrMsg(errMsg);
		return cb;
	}
	
	private List<PromotionImportVO> convertDataImportExcelPromotionNew (List<List<String>> infoPromotions, List<List<String>> infoPromotionDetails, List<List<String>> infoPromotionShops, List<CellBean> infoPromotionError, List<CellBean> infoPromotionDetailError, List<CellBean> infoPromotionShopError) {
		List<PromotionImportVO> promotionImportVOs = new ArrayList<>();
		boolean isExist = false;
		final List<String> UNIT = Arrays.asList(R.getResource("ctkm.import.new.le"), R.getResource("ctkm.import.new.thung"));
		final List<String> ALLOWED_RECURSIVE_TYPES = Arrays.asList("ZV02","ZV03","ZV05","ZV06","ZV08","ZV09","ZV11","ZV12","ZV13","ZV14","ZV15","ZV16","ZV17","ZV18","ZV20","ZV21","ZV23","ZV24");
		final List<String> ALLOWED_MUTIPLE_TYPES = Arrays.asList("ZV02","ZV03","ZV05","ZV06","ZV08","ZV09","ZV11","ZV12","ZV14","ZV15","ZV17","ZV18","ZV20","ZV21","ZV23","ZV24");
		final List<String> FREE_ITEM_TYPES = Arrays.asList("ZV03","ZV06","ZV09","ZV12","ZV15","ZV18","ZV21","ZV24");
		
		//Sheet CTKM
		for(List<String> infoPromotion:infoPromotions) {
			// Danh sach CTKM
			PromotionImportVO  promotionImportVO = new PromotionImportVO();
			//Danh sach don vi tham gia
			List<PromotionImportShopVO> listPromotionImportShop = new ArrayList<>();
						
			for(PromotionImportVO promotionImportNewVOcheck: promotionImportVOs) {
				if(infoPromotion.get(0).equals(promotionImportNewVOcheck.getPromotionCode())){
					promotionImportVO = promotionImportNewVOcheck;
					isExist = true;
					break;
				}
			}
			
			// ghi de lai cac attribute cu
			if(!isExist) {
				promotionImportVOs.add(promotionImportVO);
			}
			isExist = false;				
			promotionImportVO.setPromotionCode(!StringUtil.isNullOrEmpty(infoPromotion.get(0)) ? infoPromotion.get(0).toUpperCase() : null);
			promotionImportVO.setPromotionName(!StringUtil.isNullOrEmpty(infoPromotion.get(1)) ? infoPromotion.get(1) : null);
			promotionImportVO.setVersion(!StringUtil.isNullOrEmpty(infoPromotion.get(2)) ? infoPromotion.get(2) : null);
			promotionImportVO.setType(!StringUtil.isNullOrEmpty(infoPromotion.get(3)) ? infoPromotion.get(3).toUpperCase() : null);
			promotionImportVO.setFromDate(!StringUtil.isNullOrEmpty(infoPromotion.get(4)) ? DateUtil.parse(infoPromotion.get(4), DateUtil.DATE_FORMAT_STR) : null);
			promotionImportVO.setToDate(!StringUtil.isNullOrEmpty(infoPromotion.get(5)) ? DateUtil.parse(infoPromotion.get(5), DateUtil.DATE_FORMAT_STR) : null);
			promotionImportVO.setNotice(!StringUtil.isNullOrEmpty(infoPromotion.get(6)) ? infoPromotion.get(6).toUpperCase() : null);
			promotionImportVO.setDescriptionProduct(!StringUtil.isNullOrEmpty(infoPromotion.get(7)) ? infoPromotion.get(7) : null);
			promotionImportVO.setDescription(!StringUtil.isNullOrEmpty(infoPromotion.get(8)) ? infoPromotion.get(8) : null);
			
			if (!StringUtil.isNullOrEmpty(infoPromotion.get(9))&&"1".equals(infoPromotion.get(9))&&ALLOWED_MUTIPLE_TYPES.contains(promotionImportVO.getType())) {
				promotionImportVO.setMultiple(true);
			} else {
				promotionImportVO.setMultiple(false);
			}
			if (!StringUtil.isNullOrEmpty(infoPromotion.get(10))&&"1".equals(infoPromotion.get(10))&&ALLOWED_RECURSIVE_TYPES.contains(promotionImportVO.getType())) {
				promotionImportVO.setRecursive(true);
			} else {
				promotionImportVO.setRecursive(false);
			}
			
			promotionImportVO.setRewardType(!StringUtil.isNullOrEmpty(infoPromotion.get(11)) ? Integer.parseInt(infoPromotion.get(11)) : null);
			promotionImportVO.setApplyFromDate(!StringUtil.isNullOrEmpty(infoPromotion.get(12)) ? DateUtil.parse(infoPromotion.get(12), DateUtil.DATE_FORMAT_STR) : null);
			promotionImportVO.setApplyToDate(!StringUtil.isNullOrEmpty(infoPromotion.get(13)) ? DateUtil.parse(infoPromotion.get(13), DateUtil.DATE_FORMAT_STR) : null);
			promotionImportVO.setDiscountType(!StringUtil.isNullOrEmpty(infoPromotion.get(14)) ? Integer.parseInt(infoPromotion.get(14)) : 0);
			
			// xu ly cho danh sach đon vi tham gia
			for(List<String> infoPromotionShop : infoPromotionShops) {
				if(infoPromotionShop.get(0).equalsIgnoreCase(promotionImportVO.getPromotionCode())) {
					PromotionImportShopVO promotionImportShopVO = new PromotionImportShopVO();
					int index = checkDupShopForPromotion(listPromotionImportShop, infoPromotionShop.get(1));
					if (index != -1) {
						promotionImportShopVO = listPromotionImportShop.get(index);
					}
					
					promotionImportShopVO.setShopCode(!StringUtil.isNullOrEmpty(infoPromotionShop.get(1)) ? infoPromotionShop.get(1) : null);
					promotionImportShopVO.setQuantity(!StringUtil.isNullOrEmpty(infoPromotionShop.get(2)) ? Integer.parseInt(infoPromotionShop.get(2)) : null);
					promotionImportShopVO.setAmount(!StringUtil.isNullOrEmpty(infoPromotionShop.get(3)) ? new BigDecimal(infoPromotionShop.get(3)) : null);
					promotionImportShopVO.setNum(!StringUtil.isNullOrEmpty(infoPromotionShop.get(4)) ? new BigDecimal(infoPromotionShop.get(4)) : null);			
					listPromotionImportShop.add(promotionImportShopVO);
				}
			}
			promotionImportVO.setShops(listPromotionImportShop);
			
			// danh sach co cau
			for(List<String> infoPromotionDetail : infoPromotionDetails) {
				if(infoPromotionDetail.get(0).equalsIgnoreCase(promotionImportVO.getPromotionCode())) {
					if(promotionImportVO.getProductGroups() == null) {
						promotionImportVO.setProductGroups(new ArrayList<PromotionImportGroupVO>());
					}
					//tao nhóm cho CTKM
					PromotionImportGroupVO groupNewVO = new PromotionImportGroupVO();
					for(int i = 0; i < promotionImportVO.getProductGroups().size(); i++) {
						if(promotionImportVO.getProductGroups().get(i).getGroupCode().equals(infoPromotionDetail.get(2))) {
							groupNewVO = promotionImportVO.getProductGroups().get(i);
							isExist = true;
							break;
						}
					}
					if(!isExist) {
						promotionImportVO.getProductGroups().add(groupNewVO);
					}
					isExist = false;
					//set thong tin nhoms
					groupNewVO.setGroupCode(infoPromotionDetail.get(2));
					groupNewVO.setGroupName(groupNewVO.getGroupCode());
					groupNewVO.setMultiple(promotionImportVO.isMultiple());
					groupNewVO.setRecursive(promotionImportVO.isRecursive());
					if(isParentHeader(infoPromotionDetail))
					{
						groupNewVO.setUnit(getUnitForPromotionNew(UNIT,infoPromotionDetail.get(8)));
					}
					// tao muc cho CTKM(Mua - KM)
					List<PromotionImportGroupLevelVO> groupLevelBuys = new ArrayList<>();
					List<PromotionImportGroupLevelVO> groupLevelKMs = new ArrayList<>();					
					if(groupNewVO.getGroupLevelBuys() == null) {
						groupNewVO.setGroupLevelBuys(groupLevelBuys);
						groupNewVO.setGroupLevelKMs(groupLevelKMs);
					} else {
						groupLevelBuys = groupNewVO.getGroupLevelBuys();
						groupLevelKMs = groupNewVO.getGroupLevelKMs();
					}
					PromotionImportGroupLevelVO groupLevelVOMua = new PromotionImportGroupLevelVO();
					PromotionImportGroupLevelVO groupLevelVOKM = new PromotionImportGroupLevelVO();
					for(int i = 0; i < groupNewVO.getGroupLevelBuys().size(); i++) {
						if(groupNewVO.getGroupLevelBuys().get(i).getGroupLevelCode().equals(infoPromotionDetail.get(3))) {
							groupLevelVOMua = groupNewVO.getGroupLevelBuys().get(i);
							groupLevelVOKM = groupNewVO.getGroupLevelKMs().get(i);
							isExist = true;
							break;
						}
					}
					
					if(!isExist) {
						groupLevelBuys.add(groupLevelVOMua);
						groupLevelKMs.add(groupLevelVOKM);
					}
					isExist = false;
					// set thong tin chung cho muc
					groupLevelVOMua.setGroupLevelCode(infoPromotionDetail.get(3));
					groupLevelVOKM.setGroupLevelCode(infoPromotionDetail.get(3));
					if(isParentHeader(infoPromotionDetail)) {
						groupLevelVOMua.setQuantity(StringUtil.isNullOrEmpty(infoPromotionDetail.get(7))? null:Integer.parseInt(infoPromotionDetail.get(7)));
						groupLevelVOMua.setAmount(StringUtil.isNullOrEmpty(infoPromotionDetail.get(9))? null:new BigDecimal(infoPromotionDetail.get(9)));
						groupLevelVOMua.setUnit(getUnitForPromotionNew(UNIT, infoPromotionDetail.get(8)));
						
						groupLevelVOKM.setQuantity(StringUtil.isNullOrEmpty(infoPromotionDetail.get(14))? null:Integer.parseInt(infoPromotionDetail.get(14)));
						groupLevelVOKM.setAmount(StringUtil.isNullOrEmpty(infoPromotionDetail.get(11))? null:new BigDecimal(infoPromotionDetail.get(11)));
						groupLevelVOKM.setPercent(StringUtil.isNullOrEmpty(infoPromotionDetail.get(12))? null:Float.parseFloat((infoPromotionDetail.get(12))));
						groupLevelVOKM.setUnit(1);
					}
					//detail cho muc mua
					List<PromotionImportGroupLevelProductVO> detailVOMuas = new ArrayList<>();
					if(groupLevelVOMua.getGroupLevelProduct() != null) {
						detailVOMuas = groupLevelVOMua.getGroupLevelProduct();
					} else {
						groupLevelVOMua.setGroupLevelProduct(detailVOMuas);
					}
					if(isParentContent(infoPromotionDetail)) {
						// tao detail muc mua
						PromotionImportGroupLevelProductVO detailVOMua = new PromotionImportGroupLevelProductVO();
						for(int i = 0; i < detailVOMuas.size(); i++) {
							if(detailVOMuas.get(i).getProductCode().equals(infoPromotionDetail.get(6))) {
								detailVOMua = detailVOMuas.get(i);
								isExist = true;
								break;
							}
						}						
						if(!isExist) {
							detailVOMuas.add(detailVOMua);
						}						
						isExist = false;
						//set du lieu cho detail muc mua
						detailVOMua.setProductCode(infoPromotionDetail.get(6));
						detailVOMua.setRequired("X".equalsIgnoreCase(infoPromotionDetail.get(10))? true:false);
						detailVOMua.setQuantity(StringUtil.isNullOrEmpty(infoPromotionDetail.get(7))? null:Integer.parseInt(infoPromotionDetail.get(7)));
						detailVOMua.setAmount(StringUtil.isNullOrEmpty(infoPromotionDetail.get(9))? null:new BigDecimal(infoPromotionDetail.get(9)));
						
					}
					//tao sub-level Mua
					List<PromotionImportSubGroupLevelProductVO> subGroupLevelProducts = new ArrayList<>();
					if(groupLevelVOMua.getSubGroupLevelProduct() != null) {
						subGroupLevelProducts = groupLevelVOMua.getSubGroupLevelProduct();
					} else {
						groupLevelVOMua.setSubGroupLevelProduct(subGroupLevelProducts);
					}
					PromotionImportSubGroupLevelProductVO subGroupLevelProduct = new PromotionImportSubGroupLevelProductVO();
					if(isChildHeader(infoPromotionDetail))
					{
						subGroupLevelProduct.setQuantity(StringUtil.isNullOrEmpty(infoPromotionDetail.get(7))? null:Integer.parseInt(infoPromotionDetail.get(7)));
						subGroupLevelProduct.setAmount(StringUtil.isNullOrEmpty(infoPromotionDetail.get(9))? null:new BigDecimal(infoPromotionDetail.get(9)));
						subGroupLevelProducts.add(subGroupLevelProduct);
					}
					//detail sub-level  Mua
					List<PromotionImportSubGroupLevelProductDetailVO> detailVOSubLevelDetails = new ArrayList<>();
					int lastIndexofSubLevelProducts = subGroupLevelProducts.size() - 1;
					if(lastIndexofSubLevelProducts>=0)
					{
						if(subGroupLevelProducts.get(lastIndexofSubLevelProducts).getSubGroupLevelProductDetail() != null) {
							detailVOSubLevelDetails = subGroupLevelProducts.get(lastIndexofSubLevelProducts).getSubGroupLevelProductDetail();
						} else {
							subGroupLevelProducts.get(lastIndexofSubLevelProducts).setSubGroupLevelProductDetail(detailVOSubLevelDetails);
						}
					}
					if(isChildContent(infoPromotionDetail))
					{
						// tao detail muc con 
						PromotionImportSubGroupLevelProductDetailVO detailVOSubLevel = new PromotionImportSubGroupLevelProductDetailVO();
						for(int i = 0; i < detailVOSubLevelDetails.size(); i++) {
							if(detailVOSubLevelDetails.get(i).getProductCode().equals(infoPromotionDetail.get(6))) {
								detailVOSubLevel = detailVOSubLevelDetails.get(i);
								isExist = true;
								break;
							}
						}						
						if(!isExist) {
							detailVOSubLevelDetails.add(detailVOSubLevel);
						}						
						isExist = false;
						//set du lieu cho detail muc mua
						detailVOSubLevel.setProductCode(infoPromotionDetail.get(6));
						detailVOSubLevel.setRequired("X".equalsIgnoreCase(infoPromotionDetail.get(10))? true:false);
					}
					
					if(FREE_ITEM_TYPES.contains(promotionImportVO.getType()))
					{
					//detail cho muc KM
					List<PromotionImportGroupLevelProductVO> detailVOKMs = new ArrayList<>();
					if(groupLevelVOKM.getGroupLevelProduct() != null) {
						detailVOKMs = groupLevelVOKM.getGroupLevelProduct();
					} else {
						groupLevelVOKM.setGroupLevelProduct(detailVOKMs);
					}
					// tao detail muc KM
					PromotionImportGroupLevelProductVO detailVOKM = new PromotionImportGroupLevelProductVO();
					isExist = false;
					for(int i = 0; i < detailVOKMs.size(); i++) {
						if(detailVOKMs.get(i).getProductCode().equals(infoPromotionDetail.get(13))) {
							detailVOKM = detailVOKMs.get(i);
							isExist = true;
							break;
						}
					}						
											
					//set du lieu cho detail muc KM
					if(!infoPromotionDetail.get(13).isEmpty())	
					{
						if(!isExist) {
							detailVOKMs.add(detailVOKM);
						}
						detailVOKM.setProductCode(infoPromotionDetail.get(13));
						detailVOKM.setRequired("X".equalsIgnoreCase(infoPromotionDetail.get(16))? true:false);
						detailVOKM.setQuantity(StringUtil.isNullOrEmpty(infoPromotionDetail.get(14))? null:Integer.parseInt(infoPromotionDetail.get(15)));			
						detailVOKM.setAmount(StringUtil.isNullOrEmpty(infoPromotionDetail.get(11))? null:new BigDecimal(infoPromotionDetail.get(11)));	
						detailVOKM.setPercent(StringUtil.isNullOrEmpty(infoPromotionDetail.get(12))? null:Integer.parseInt(infoPromotionDetail.get(12)));	
					}
				}
				else
				{
					if(isParentHeader(infoPromotionDetail))
					{
						groupLevelVOKM.setAmount(StringUtil.isNullOrEmpty(infoPromotionDetail.get(11))? null:new BigDecimal(infoPromotionDetail.get(11)));	
						groupLevelVOKM.setPercent(StringUtil.isNullOrEmpty(infoPromotionDetail.get(12))? null:Float.parseFloat(infoPromotionDetail.get(12)));
					}
				}
			  }
			}
		  }
		return promotionImportVOs;
	}
	
	private void resetProgramMultiple(PromotionImportVO importNewVO) {
		if(importNewVO.isMultiple()) {
			importNewVO.setMultiple(false);
		}
	}
	
	private void resetProgramRecursive(PromotionImportVO importNewVO) {
		if(importNewVO.isRecursive()) {
			importNewVO.setRecursive(false);
		}
	}
	
	private void resetProgramDiscountType(PromotionImportVO importNewVO) {
		importNewVO.setDiscountType(0);
	}
	
	private void resetProgramVoucherTime(PromotionImportVO importNewVO) {
		importNewVO.setApplyFromDate(null);
		importNewVO.setApplyToDate(null);
	}
	
	
	/**
	 * validate nhóm KM: conditonType
	 * 	1: tiền
	 * 	2: số lượng(sản phẩm)
	 * 	3: %
	 * validate nhóm điều kiện đăng ký cha: conditonType
	 * 	1: tiền
	 * 	2: số lượng(sản phẩm)
	 * validate nhóm điều kiện đăng ký con: conditonType
	 * 	1: tiền
	 * 	2: số lượng(sản phẩm)
	 * */
	
	final Integer CONDITION_TYPE_AMOUNT = 1;
	final Integer CONDITION_TYPE_QUANTITY = 2;
	final Integer CONDITION_TYPE_PERCENT = 3;
	
	private String validateVoucher(PromotionImportVO importNewVO) {
		if(((Integer) 2).equals(importNewVO.getRewardType()) && Arrays.asList(ConstantManager.getSaleOrderBillPromotionVoucherTypeCode()).contains(importNewVO.getType())) {
			if(importNewVO.getApplyFromDate() == null) {
				importNewVO.setMessageError(R.getResource("catalog.promotion.import.column.null", R.getResource("imp.epx.tuyen.clmn.tuNgay.traThuong")));
				return R.getResource("catalog.promotion.import.column.null", R.getResource("imp.epx.tuyen.clmn.tuNgay.traThuong"));
			}
			if(DateUtil.compareDateWithoutTime(importNewVO.getApplyFromDate(), DateUtil.now()) < 0) {
				importNewVO.setMessageError(R.getResource("common.date.greater.currentdate", R.getResource("imp.epx.tuyen.clmn.tuNgay.traThuong")));
				return R.getResource("common.date.greater.currentdate", R.getResource("imp.epx.tuyen.clmn.tuNgay.traThuong"));
			}
			
			if(importNewVO.getApplyToDate() != null && DateUtil.compareDateWithoutTime(importNewVO.getApplyFromDate(), importNewVO.getApplyToDate()) == 1) {
				importNewVO.setMessageError(R.getResource("common.compare.error.less.or.equal.tow.param", R.getResource("imp.epx.tuyen.clmn.tuNgay.traThuong"), R.getResource("imp.epx.tuyen.clmn.denNgay.traThuong")));
				return R.getResource("common.compare.error.less.or.equal.tow.param", R.getResource("imp.epx.tuyen.clmn.tuNgay.traThuong"), R.getResource("imp.epx.tuyen.clmn.denNgay.traThuong"));
			}
		}
		return "";
	}
	
	private String validatePromotionGroupLevelUnit(PromotionImportVO importNewVO, PromotionImportGroupVO groupNewVO) {
		if(groupNewVO.getGroupLevelBuys() != null && groupNewVO.getGroupLevelBuys().size() > 0) {
			int size = groupNewVO.getGroupLevelBuys().size();
			for(int i = 0; i < (size - 1); i++) {
				PromotionImportGroupLevelVO groupLevelBuyEx1 = groupNewVO.getGroupLevelBuys().get(i);
				PromotionImportGroupLevelVO groupLevelBuyEx2 = groupNewVO.getGroupLevelBuys().get(i + 1);
				if(groupLevelBuyEx1.getUnit() != groupLevelBuyEx2.getUnit()) {
					groupNewVO.setMessageError(R.getResource("ctkm.import.new.condition.level.dvt", importNewVO.getPromotionCode(), groupNewVO.getGroupCode()));
					return R.getResource("ctkm.import.new.condition.level.dvt", importNewVO.getPromotionCode(), groupNewVO.getGroupCode());
				}
			}
		}
		return "";
	}
	
	private String validatePromotionMultiGroup(PromotionImportVO importNewVO) {
		if(importNewVO.getProductGroups() != null && importNewVO.getProductGroups().size() > 0) {
			if(importNewVO.getProductGroups().size() > 1 && !PromotionProgramMgr.PROMOTION_TYPE_MULTI_GROUPS.contains(importNewVO.getType())) {
				importNewVO.setMessageError(R.getResource("ctkm.import.new.product.group.multi", importNewVO.getType()));
				return R.getResource("ctkm.import.new.product.group.multi", importNewVO.getType());
			}
		}
		return "";
	}
	
	private String validatePromotionMultiGroupLevelProduct(PromotionImportVO importNewVO, PromotionImportGroupVO groupNewVO) {
		if(groupNewVO.getGroupLevelBuys() != null && groupNewVO.getGroupLevelBuys().size() > 0) {
			int size = groupNewVO.getGroupLevelBuys().size();
			for(int i = 0; i < size; i++) {
				PromotionImportGroupLevelVO groupLevelBuy = groupNewVO.getGroupLevelBuys().get(i);
				if(groupLevelBuy.getGroupLevelProduct() == null || groupLevelBuy.getGroupLevelProduct().size() == 0) {
					groupLevelBuy.setMessageError(R.getResource("ctkm.import.new.condition.line.product.null", importNewVO.getPromotionCode(), groupNewVO.getGroupCode(), groupLevelBuy.getGroupLevelCode()));
					return R.getResource("ctkm.import.new.condition.line.product.null", importNewVO.getPromotionCode(), groupNewVO.getGroupCode(), groupLevelBuy.getGroupLevelCode());
				}
				
				if(groupLevelBuy.getGroupLevelProduct().size() > 1) {
					groupLevelBuy.setMessageError(R.getResource("ctkm.import.new.condition.line.multi.product.level", importNewVO.getPromotionCode(), groupNewVO.getGroupCode(), groupLevelBuy.getGroupLevelCode()));
					return R.getResource("ctkm.import.new.condition.line.multi.product.level", importNewVO.getPromotionCode(), groupNewVO.getGroupCode(), groupLevelBuy.getGroupLevelCode());
				}
			}
		}
		return "";
	}
	
	private String validatePromotionProductIsNotHomogeneous (PromotionImportVO importNewVO, PromotionImportGroupVO groupNewVO) {
		if(groupNewVO.getGroupLevelBuys() != null && groupNewVO.getGroupLevelBuys().size() > 0) {
			int size = groupNewVO.getGroupLevelBuys().size();
			List<String> rootProductCodes = new ArrayList<>();
			if(groupNewVO.getGroupLevelBuys().get(0).getGroupLevelProduct() != null && groupNewVO.getGroupLevelBuys().get(0).getGroupLevelProduct().size() > 0) {
				for(PromotionImportGroupLevelProductVO groupLevelProduct : groupNewVO.getGroupLevelBuys().get(0).getGroupLevelProduct()) {
					if (rootProductCodes.indexOf(groupLevelProduct.getProductCode()) == -1) {
						rootProductCodes.add(groupLevelProduct.getProductCode());
					}
				}						
			}
			
			for(int i = 0; i < (size - 1); i++) {
				PromotionImportGroupLevelVO groupLevelBuyEx1 = groupNewVO.getGroupLevelBuys().get(i);						
				if(groupLevelBuyEx1.getGroupLevelProduct() == null || groupLevelBuyEx1.getGroupLevelProduct().size() == 0) {
					groupLevelBuyEx1.setMessageError(R.getResource("ctkm.import.new.condition.product.not.found", importNewVO.getPromotionCode(), groupNewVO.getGroupCode(), groupLevelBuyEx1.getGroupLevelCode()));
					return R.getResource("ctkm.import.new.condition.product.not.found", importNewVO.getPromotionCode(), groupNewVO.getGroupCode(), groupLevelBuyEx1.getGroupLevelCode());
				}
				List<String> lstProductCode = new ArrayList<>();
				for(PromotionImportGroupLevelProductVO groupLevelProduct : groupLevelBuyEx1.getGroupLevelProduct()) {
					if (lstProductCode.indexOf(groupLevelProduct.getProductCode()) == -1) {
						lstProductCode.add(groupLevelProduct.getProductCode());
					} else {
						groupLevelBuyEx1.setMessageError(R.getResource("ctkm.import.new.condition.product.is.duplicate", importNewVO.getPromotionCode(), groupNewVO.getGroupCode(), groupLevelBuyEx1.getGroupLevelCode()));
						return R.getResource("ctkm.import.new.condition.product.is.duplicate", importNewVO.getPromotionCode(), groupNewVO.getGroupCode(), groupLevelBuyEx1.getGroupLevelCode());
					}
				}
				
				if(lstProductCode.size() != rootProductCodes.size()) {
					groupLevelBuyEx1.setMessageError(R.getResource("ctkm.import.new.condition.product.is.not.homogeneous", importNewVO.getPromotionCode(), groupNewVO.getGroupCode()));
					return R.getResource("ctkm.import.new.condition.product.is.not.homogeneous", importNewVO.getPromotionCode(), groupNewVO.getGroupCode());
				}

				for(PromotionImportGroupLevelProductVO groupLevelProduct : groupLevelBuyEx1.getGroupLevelProduct()) {
					if(rootProductCodes.indexOf(groupLevelProduct.getProductCode()) == -1) {
						groupLevelBuyEx1.setMessageError(R.getResource("ctkm.import.new.condition.product.is.not.homogeneous", importNewVO.getPromotionCode(), groupNewVO.getGroupCode()));
						return R.getResource("ctkm.import.new.condition.product.is.not.homogeneous", importNewVO.getPromotionCode(), groupNewVO.getGroupCode());
					}
				}						
			}
		}
		return "";
	}
	
	private String validatePromotionGroupLevelConditionQuantityOrAmount(PromotionImportVO importNewVO, Integer conditionType, PromotionImportGroupVO groupNewVO) {
		int size = groupNewVO.getGroupLevelBuys().size();
		for(int i = 0; i < (size - 1); i++) {
			for(int j = i+1; j < size; j++) {
				PromotionImportGroupLevelVO groupLevelBuyEx1 = groupNewVO.getGroupLevelBuys().get(i);
				PromotionImportGroupLevelVO groupLevelBuyEx2 = groupNewVO.getGroupLevelBuys().get(j);
				if(CONDITION_TYPE_AMOUNT.equals(conditionType)) {
					if(groupLevelBuyEx1.getAmount().equals(groupLevelBuyEx2.getAmount())) {
						groupLevelBuyEx1.setMessageError(R.getResource("ctkm.import.new.condition.doc.group.level.amount.duplicate", importNewVO.getPromotionCode(), groupNewVO.getGroupCode(), groupLevelBuyEx1.getGroupLevelCode()));
						return R.getResource("ctkm.import.new.condition.doc.group.level.amount.duplicate", importNewVO.getPromotionCode(), groupNewVO.getGroupCode(), groupLevelBuyEx1.getGroupLevelCode());
					}
				} else {
					if(groupLevelBuyEx1.getQuantity().equals(groupLevelBuyEx2.getQuantity())) {
						groupLevelBuyEx1.setMessageError(R.getResource("ctkm.import.new.condition.doc.group.level.quantity.duplicate", importNewVO.getPromotionCode(), groupNewVO.getGroupCode(), groupLevelBuyEx1.getGroupLevelCode()));
						return R.getResource("ctkm.import.new.condition.doc.group.level.quantity.duplicate", importNewVO.getPromotionCode(), groupNewVO.getGroupCode(), groupLevelBuyEx1.getGroupLevelCode());
					}
				}
			}
		}
		return "";
	}
	
	private String validatePromotionQuantityOfGroupLevel (PromotionImportVO importNewVO, PromotionImportGroupVO groupNewVO) {
		final List<String> PROMOTION_TYPE_GROUP_QUANTITY = Arrays.asList(PromotionType.ZV07.getValue(), PromotionType.ZV08.getValue(), PromotionType.ZV09.getValue());
		if(PROMOTION_TYPE_GROUP_QUANTITY.contains(importNewVO.getType()) && groupNewVO.getGroupLevelBuys() != null && groupNewVO.getGroupLevelBuys().size() > 0) {
			int size = groupNewVO.getGroupLevelBuys().size();
			Map<String, List<Integer>> mProductByLevel = new HashMap<>();
			for(int i = 0; i < (size - 1); i++) {
				PromotionImportGroupLevelVO groupLevelBuy = groupNewVO.getGroupLevelBuys().get(i);
				
				if(groupLevelBuy.getGroupLevelProduct() != null && groupLevelBuy.getGroupLevelProduct().size() > 0) {
					for(PromotionImportGroupLevelProductVO product : groupLevelBuy.getGroupLevelProduct()) {
						if(!mProductByLevel.containsKey(product.getProductCode())) {
							mProductByLevel.put(product.getProductCode(), new ArrayList<Integer>());
						}
						
						mProductByLevel.get(product.getProductCode()).add(product.getQuantity());
					}
				}
			}
			
			for(Map.Entry<String, List<Integer>> productInfo : mProductByLevel.entrySet()) {
				List<Integer> quantitys = productInfo.getValue();
				for(int i = 0; i < (quantitys.size()-1); i++) {
					if(quantitys.get(i) != null && quantitys.get(i).compareTo(0) > 0) {
						for(int j = i+1; j < quantitys.size(); j++) {
							if(quantitys.get(j) == null || quantitys.get(j).compareTo(0) <= 0) {
								continue;
							}
							
							if(quantitys.get(i).equals(quantitys.get(j))) {
								groupNewVO.setMessageError(R.getResource("ctkm.import.new.condition.group.quantity.is.not.homogeneous", importNewVO.getPromotionCode(), groupNewVO.getGroupCode()));
								return R.getResource("ctkm.import.new.condition.group.quantity.is.not.homogeneous", importNewVO.getPromotionCode(), groupNewVO.getGroupCode());
							}
						}
					}
				}
				
			}
		}
		return "";
	}
	
	private String validatePromotionAmountOfGroupLevel (PromotionImportVO importNewVO, PromotionImportGroupVO groupNewVO) {
		final List<String> PROMOTION_TYPE_GROUP_AMOUNT = Arrays.asList(PromotionType.ZV10.getValue(), PromotionType.ZV11.getValue(), PromotionType.ZV12.getValue());
		if(PROMOTION_TYPE_GROUP_AMOUNT.contains(importNewVO.getType()) && groupNewVO.getGroupLevelBuys() != null && groupNewVO.getGroupLevelBuys().size() > 0) {
			int size = groupNewVO.getGroupLevelBuys().size();
			Map<String, List<BigDecimal>> mProductByLevel = new HashMap<>();
			for(int i = 0; i < (size - 1); i++) {
				PromotionImportGroupLevelVO groupLevelBuy = groupNewVO.getGroupLevelBuys().get(i);
				
				if(groupLevelBuy.getGroupLevelProduct() != null && groupLevelBuy.getGroupLevelProduct().size() > 0) {
					for(PromotionImportGroupLevelProductVO product : groupLevelBuy.getGroupLevelProduct()) {
						if(!mProductByLevel.containsKey(product.getProductCode())) {
							mProductByLevel.put(product.getProductCode(), new ArrayList<BigDecimal>());
						}
						
						mProductByLevel.get(product.getProductCode()).add(product.getAmount());
					}
				}
			}
			
			for(Map.Entry<String, List<BigDecimal>> productInfo : mProductByLevel.entrySet()) {
				List<BigDecimal> quantitys = productInfo.getValue();
				for(int i = 0; i < (quantitys.size()-1); i++) {
					if(quantitys.get(i) != null && quantitys.get(i).compareTo(BigDecimal.ZERO) > 0) {
						for(int j = i+1; j < quantitys.size(); j++) {
							if(quantitys.get(j) == null || quantitys.get(j).compareTo(BigDecimal.ZERO) <= 0) {
								continue;
							}
							
							if(quantitys.get(i).equals(quantitys.get(j))) {
								groupNewVO.setMessageError(R.getResource("ctkm.import.new.condition.group.amount.is.not.homogeneous", importNewVO.getPromotionCode(), groupNewVO.getGroupCode()));
								return R.getResource("ctkm.import.new.condition.group.amount.is.not.homogeneous", importNewVO.getPromotionCode(), groupNewVO.getGroupCode());
							}
						}
					}
				}
			}
		}
		return "";
	}
	
	private String validatePromotionLine(PromotionImportVO importNewVO, Integer conditionType) {
		StringBuilder sbMessage = new StringBuilder();
		if(importNewVO.getProductGroups() != null && importNewVO.getProductGroups().size() > 0) {
			sbMessage.append(validatePromotionMultiGroup(importNewVO));
			if(!StringUtil.isNullOrEmpty(sbMessage.toString())) {
				return sbMessage.toString();
			}
			for(PromotionImportGroupVO groupNewVO : importNewVO.getProductGroups()) {
				if(groupNewVO.getGroupLevelBuys() != null && groupNewVO.getGroupLevelBuys().size() > 0) {
					sbMessage.append(validatePromotionMultiGroupLevelProduct(importNewVO, groupNewVO));
					if(!StringUtil.isNullOrEmpty(sbMessage.toString())) {
						return sbMessage.toString();
					}
					sbMessage.append(validatePromotionGroupLevelUnit(importNewVO, groupNewVO));
					if(!StringUtil.isNullOrEmpty(sbMessage.toString())) {
						return sbMessage.toString();
					}
					
					int size = groupNewVO.getGroupLevelBuys().size();
					for(int i = 0; i < size; i++) {
						PromotionImportGroupLevelVO groupLevelBuy = groupNewVO.getGroupLevelBuys().get(i);
						if(CONDITION_TYPE_AMOUNT.equals(conditionType)) {
							if(groupLevelBuy.getGroupLevelProduct().get(0).getAmount().compareTo(BigDecimal.ZERO) <= 0) {
								groupLevelBuy.setMessageError(R.getResource("ctkm.import.new.condition.line.product.amount.required", importNewVO.getPromotionCode(), groupNewVO.getGroupCode(), groupLevelBuy.getGroupLevelCode()));
								return R.getResource("ctkm.import.new.condition.line.product.amount.required", importNewVO.getPromotionCode(), groupNewVO.getGroupCode(), groupLevelBuy.getGroupLevelCode());
							}							
						} else {
							if(groupLevelBuy.getGroupLevelProduct().get(0).getQuantity().compareTo(0) <= 0) {
								groupLevelBuy.setMessageError(R.getResource("ctkm.import.new.condition.line.product.quantity.required", importNewVO.getPromotionCode(), groupNewVO.getGroupCode(), groupLevelBuy.getGroupLevelCode()));
								return R.getResource("ctkm.import.new.condition.line.product.quantity.required", importNewVO.getPromotionCode(), groupNewVO.getGroupCode(), groupLevelBuy.getGroupLevelCode());
							}
						}
						
						if(groupLevelBuy.getSubGroupLevelProduct() != null && groupLevelBuy.getSubGroupLevelProduct().size() > 0) {
							importNewVO.setMessageError(R.getResource("ctkm.import.new.condition.doc.sub.condition", importNewVO.getType(), groupNewVO.getGroupCode(), groupLevelBuy.getGroupLevelCode()));
							return R.getResource("ctkm.import.new.condition.doc.sub.condition", importNewVO.getType(), groupNewVO.getGroupCode(), groupLevelBuy.getGroupLevelCode());
						}
						
						String productCodeEx1 = groupLevelBuy.getGroupLevelProduct().get(0).getProductCode();
						String productCodeEx2 = groupLevelBuy.getGroupLevelProduct().get(0).getProductCode();
						if(!groupLevelBuy.getGroupLevelProduct().get(0).isRequired()) {
							groupLevelBuy.setMessageError(R.getResource("ctkm.import.new.condition.line.product.required", importNewVO.getPromotionCode(), groupNewVO.getGroupCode(), groupLevelBuy.getGroupLevelCode(), productCodeEx1));
							return R.getResource("ctkm.import.new.condition.line.product.required", importNewVO.getPromotionCode(), groupNewVO.getGroupCode(), groupLevelBuy.getGroupLevelCode(), productCodeEx1);
						}
						if((i +1) < size) {
							productCodeEx2 = groupNewVO.getGroupLevelBuys().get(i+1).getGroupLevelProduct().get(0).getProductCode();
						}
						if(!productCodeEx1.equals(productCodeEx2)) {
							groupLevelBuy.setMessageError(R.getResource("ctkm.import.new.condition.product.is.not.homogeneous", importNewVO.getPromotionCode(), groupNewVO.getGroupCode(), groupLevelBuy.getGroupLevelCode()));
							return R.getResource("ctkm.import.new.condition.product.is.not.homogeneous", importNewVO.getPromotionCode(), groupNewVO.getGroupCode(), groupLevelBuy.getGroupLevelCode());
						}
					}
				}
			}
		}
		return sbMessage.toString();
	}	
	
	private String validatePromotionGroup(PromotionImportVO importNewVO, Integer conditionType) {
		StringBuilder sbMessage = new StringBuilder();
		final List<String> PROMOTION_TYPE_GROUP =  Arrays.asList(PromotionType.ZV07.getValue(), PromotionType.ZV10.getValue());
		if(importNewVO.getProductGroups() != null && importNewVO.getProductGroups().size() > 0) {
			sbMessage.append(validatePromotionMultiGroup(importNewVO));
			if(!StringUtil.isNullOrEmpty(sbMessage.toString())) {
				return sbMessage.toString();
			}
			
			for(PromotionImportGroupVO groupNewVO : importNewVO.getProductGroups()) {
				sbMessage.append(validatePromotionProductIsNotHomogeneous(importNewVO, groupNewVO));
				if(!StringUtil.isNullOrEmpty(sbMessage.toString())) {
					return sbMessage.toString();
				}
				
				sbMessage.append(validatePromotionQuantityOfGroupLevel(importNewVO, groupNewVO));
				if(!StringUtil.isNullOrEmpty(sbMessage.toString())) {
					return sbMessage.toString();
				}
				
				sbMessage.append(validatePromotionAmountOfGroupLevel(importNewVO, groupNewVO));
				if(!StringUtil.isNullOrEmpty(sbMessage.toString())) {
					return sbMessage.toString();
				}
				
				if(groupNewVO.getGroupLevelBuys() != null && groupNewVO.getGroupLevelBuys().size() > 0) {
					sbMessage.append(validatePromotionGroupLevelUnit(importNewVO, groupNewVO));
					if(!StringUtil.isNullOrEmpty(sbMessage.toString())) {
						return sbMessage.toString();
					}
					
					int size = groupNewVO.getGroupLevelBuys().size();
					for(int i = 0; i < size; i++) {
						PromotionImportGroupLevelVO groupLevelBuy = groupNewVO.getGroupLevelBuys().get(i);
						if(groupLevelBuy.getGroupLevelProduct() == null || groupLevelBuy.getGroupLevelProduct().size() <= 0) {
							groupLevelBuy.setMessageError(R.getResource("ctkm.import.new.condition.line.multi.product", importNewVO.getPromotionCode(), groupNewVO.getGroupCode(), groupLevelBuy.getGroupLevelCode()));
							return R.getResource("ctkm.import.new.condition.line.multi.product", importNewVO.getPromotionCode(), groupNewVO.getGroupCode(), groupLevelBuy.getGroupLevelCode());
						}
						
						Integer totalQuantity = 0;
						BigDecimal totalAmount = BigDecimal.ZERO;
						for(PromotionImportGroupLevelProductVO levelProduct : groupLevelBuy.getGroupLevelProduct()) {
							if(CONDITION_TYPE_AMOUNT.equals(conditionType)) {
								if(levelProduct.getAmount() != null && levelProduct.getAmount().compareTo(BigDecimal.ZERO) < 0) {
									groupLevelBuy.setMessageError(R.getResource("ctkm.import.new.condition.group.product.amount.invalid", importNewVO.getPromotionCode(), groupNewVO.getGroupCode(), groupLevelBuy.getGroupLevelCode()));
									return R.getResource("ctkm.import.new.condition.group.product.amount.invalid", importNewVO.getPromotionCode(), groupNewVO.getGroupCode(), groupLevelBuy.getGroupLevelCode());
								}
							} else {
								if(levelProduct.getQuantity() != null && levelProduct.getQuantity().compareTo(0) < 0) {
									groupLevelBuy.setMessageError(R.getResource("ctkm.import.new.condition.group.product.quantity.invalid", importNewVO.getPromotionCode(), groupNewVO.getGroupCode(), groupLevelBuy.getGroupLevelCode()));
									return R.getResource("ctkm.import.new.condition.group.product.quantity.invalid", importNewVO.getPromotionCode(), groupNewVO.getGroupCode(), groupLevelBuy.getGroupLevelCode());
								}
							}
							
							Integer quantity = levelProduct.getQuantity()== null? 0: levelProduct.getQuantity();
							BigDecimal amount = levelProduct.getAmount()== null? BigDecimal.ZERO: levelProduct.getAmount();
							totalQuantity += quantity;
							totalAmount = totalAmount.add(amount);
							
							if(!PROMOTION_TYPE_GROUP.contains(importNewVO.getType())) {
								if(CONDITION_TYPE_AMOUNT.equals(conditionType)) {
									if(levelProduct.getAmount() != null && !levelProduct.isRequired()) {
										groupLevelBuy.setMessageError(R.getResource("ctkm.import.new.condition.group.product.amount.required", importNewVO.getPromotionCode(), groupNewVO.getGroupCode(), groupLevelBuy.getGroupLevelCode(), levelProduct.getProductCode()));
										return R.getResource("ctkm.import.new.condition.group.product.amount.required", importNewVO.getPromotionCode(), groupNewVO.getGroupCode(), groupLevelBuy.getGroupLevelCode(), levelProduct.getProductCode());
									}
								} else {
									if(levelProduct.getQuantity() != null && !levelProduct.isRequired()) {
										groupLevelBuy.setMessageError(R.getResource("ctkm.import.new.condition.group.product.quantity.required", importNewVO.getPromotionCode(), groupNewVO.getGroupCode(), groupLevelBuy.getGroupLevelCode(), levelProduct.getProductCode()));
										return R.getResource("ctkm.import.new.condition.group.product.quantity.required", importNewVO.getPromotionCode(), groupNewVO.getGroupCode(), groupLevelBuy.getGroupLevelCode(), levelProduct.getProductCode());
									}
								}
							}
						}
						
						if(CONDITION_TYPE_AMOUNT.equals(conditionType)) {
							if(groupLevelBuy.getAmount() == null || groupLevelBuy.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
								groupLevelBuy.setMessageError(R.getResource("ctkm.import.new.condition.group.amount.is.not.empty", importNewVO.getPromotionCode(), groupNewVO.getGroupCode(), groupLevelBuy.getGroupLevelCode()));
								return R.getResource("ctkm.import.new.condition.group.amount.is.not.empty", importNewVO.getPromotionCode(), groupNewVO.getGroupCode(), groupLevelBuy.getGroupLevelCode());
							}
							
							if(totalAmount.compareTo(groupLevelBuy.getAmount()) > 0) {
								groupLevelBuy.setMessageError(R.getResource("ctkm.import.new.condition.group.greater.amount", importNewVO.getPromotionCode(), groupNewVO.getGroupCode(), groupLevelBuy.getGroupLevelCode()));
								return R.getResource("ctkm.import.new.condition.group.greater.amount", importNewVO.getPromotionCode(), groupNewVO.getGroupCode(), groupLevelBuy.getGroupLevelCode());
							}
						} else {
							if(groupLevelBuy.getQuantity() == null || groupLevelBuy.getQuantity().compareTo(0) <= 0) {
								groupLevelBuy.setMessageError(R.getResource("ctkm.import.new.condition.group.quantity.is.not.empty", importNewVO.getPromotionCode(), groupNewVO.getGroupCode(), groupLevelBuy.getGroupLevelCode()));
								return R.getResource("ctkm.import.new.condition.group.quantity.is.not.empty", importNewVO.getPromotionCode(), groupNewVO.getGroupCode(), groupLevelBuy.getGroupLevelCode());
							}
							
							if(totalQuantity.compareTo(groupLevelBuy.getQuantity()) > 0) {
								groupLevelBuy.setMessageError(R.getResource("ctkm.import.new.condition.group.greater.quantity", importNewVO.getPromotionCode(), groupNewVO.getGroupCode(), groupLevelBuy.getGroupLevelCode()));
								return R.getResource("ctkm.import.new.condition.group.greater.quantity", importNewVO.getPromotionCode(), groupNewVO.getGroupCode(), groupLevelBuy.getGroupLevelCode());
							}
						}
					}
				}
			}			
		}
		return sbMessage.toString();
	}
	
	private String validatePromotionBundle(PromotionImportVO importNewVO, Integer conditionType) {
		StringBuilder sbMessage = new StringBuilder();
		if(importNewVO.getProductGroups() != null && importNewVO.getProductGroups().size() > 0) {
			sbMessage.append(validatePromotionMultiGroup(importNewVO));
			if(!StringUtil.isNullOrEmpty(sbMessage.toString())) {
				return sbMessage.toString();
			}
			for(PromotionImportGroupVO groupNewVO : importNewVO.getProductGroups()) {
				sbMessage.append(validatePromotionProductIsNotHomogeneous(importNewVO, groupNewVO));
				if(!StringUtil.isNullOrEmpty(sbMessage.toString())) {
					return sbMessage.toString();
				}
				if(groupNewVO.getGroupLevelBuys() != null && groupNewVO.getGroupLevelBuys().size() > 0) {
					sbMessage.append(validatePromotionGroupLevelUnit(importNewVO, groupNewVO));
					if(!StringUtil.isNullOrEmpty(sbMessage.toString())) {
						return sbMessage.toString();
					}
					
					int size = groupNewVO.getGroupLevelBuys().size();
					for(int i = 0; i < size; i++) {
						PromotionImportGroupLevelVO groupLevelBuy = groupNewVO.getGroupLevelBuys().get(i);
						for(PromotionImportGroupLevelProductVO groupLevelProduct : groupLevelBuy.getGroupLevelProduct()) {
							if(CONDITION_TYPE_AMOUNT.equals(conditionType)) {
								if(groupLevelProduct.getAmount()==null||groupLevelProduct.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
									groupLevelBuy.setMessageError(R.getResource("ctkm.import.new.condition.line.product.amount.required", importNewVO.getPromotionCode(), groupNewVO.getGroupCode(), groupLevelBuy.getGroupLevelCode()));
									return R.getResource("ctkm.import.new.condition.line.product.amount.required", importNewVO.getPromotionCode(), groupNewVO.getGroupCode(), groupLevelBuy.getGroupLevelCode());
								}
							} else {
								if(groupLevelProduct.getQuantity()==null||groupLevelProduct.getQuantity().compareTo(0) <= 0) {
									groupLevelBuy.setMessageError(R.getResource("ctkm.import.new.condition.line.product.quantity.required", importNewVO.getPromotionCode(), groupNewVO.getGroupCode(), groupLevelBuy.getGroupLevelCode()));
									return R.getResource("ctkm.import.new.condition.line.product.quantity.required", importNewVO.getPromotionCode(), groupNewVO.getGroupCode(), groupLevelBuy.getGroupLevelCode());
								}
							}
							if(!groupLevelProduct.isRequired()) {
								groupLevelBuy.setMessageError(R.getResource("ctkm.import.new.condition.line.product.required", importNewVO.getPromotionCode(), groupNewVO.getGroupCode(), groupLevelBuy.getGroupLevelCode(), groupLevelProduct.getProductCode()));
								return R.getResource("ctkm.import.new.condition.line.product.required", importNewVO.getPromotionCode(), groupNewVO.getGroupCode(), groupLevelBuy.getGroupLevelCode(), groupLevelProduct.getProductCode());
							}
							
							if(groupLevelBuy.getSubGroupLevelProduct() != null && groupLevelBuy.getSubGroupLevelProduct().size() > 0) {
								importNewVO.setMessageError(R.getResource("ctkm.import.new.condition.doc.sub.condition", importNewVO.getType(), groupNewVO.getGroupCode(), groupLevelBuy.getGroupLevelCode()));
								return R.getResource("ctkm.import.new.condition.doc.sub.condition", importNewVO.getType(), groupNewVO.getGroupCode(), groupLevelBuy.getGroupLevelCode());
							}
						}						
					}
				}
			}
		}
		return sbMessage.toString();
	}
	
	private String validatePromotionDoc(PromotionImportVO importNewVO, Integer conditionType) {
		StringBuilder sbMessage = new StringBuilder();		
		final List<String> PROMOTION_TYPE_DOC_AMOUNT =  Arrays.asList(PromotionType.ZV19.getValue(), PromotionType.ZV20.getValue(), PromotionType.ZV21.getValue());
		if(importNewVO.getProductGroups() != null && importNewVO.getProductGroups().size() > 0) {
			sbMessage.append(validatePromotionMultiGroup(importNewVO));
			if(!StringUtil.isNullOrEmpty(sbMessage.toString())) {
				return sbMessage.toString();
			}
			
			for(PromotionImportGroupVO groupNewVO : importNewVO.getProductGroups()) {
				if(groupNewVO.getGroupLevelBuys() != null && groupNewVO.getGroupLevelBuys().size() > 0) {
					sbMessage.append(validatePromotionGroupLevelUnit(importNewVO, groupNewVO));
					if(!StringUtil.isNullOrEmpty(sbMessage.toString())) {
						return sbMessage.toString();
					}
					
					int size = groupNewVO.getGroupLevelBuys().size();
					for(int i = 0; i < size; i++) {
						PromotionImportGroupLevelVO groupLevelBuy = groupNewVO.getGroupLevelBuys().get(i);
						if(groupLevelBuy.getGroupLevelProduct().size() > 0) {
							importNewVO.setMessageError(R.getResource("ctkm.import.new.condition.doc.is.not.product", importNewVO.getType(), groupNewVO.getGroupCode(), groupLevelBuy.getGroupLevelCode(), importNewVO.getType()));
							return R.getResource("ctkm.import.new.condition.doc.is.not.product", importNewVO.getType(), groupNewVO.getGroupCode(), groupLevelBuy.getGroupLevelCode(), importNewVO.getType());
						}
						
						if(CONDITION_TYPE_AMOUNT.equals(conditionType)) {
							if(groupLevelBuy.getAmount() == null || groupLevelBuy.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
								importNewVO.setMessageError(R.getResource("ctkm.import.new.condition.doc.group.level.amount.null", importNewVO.getType(), groupNewVO.getGroupCode(), groupLevelBuy.getGroupLevelCode()));
								return R.getResource("ctkm.import.new.condition.doc.group.level.amount.null", importNewVO.getType(), groupNewVO.getGroupCode(), groupLevelBuy.getGroupLevelCode());
							}
						} else {
							if(groupLevelBuy.getQuantity() == null || groupLevelBuy.getQuantity().compareTo(0) <= 0) {
								importNewVO.setMessageError(R.getResource("ctkm.import.new.condition.doc.group.level.quantity.null", importNewVO.getType(), groupNewVO.getGroupCode(), groupLevelBuy.getGroupLevelCode()));
								return R.getResource("ctkm.import.new.condition.doc.group.level.quantity.null", importNewVO.getType(), groupNewVO.getGroupCode(), groupLevelBuy.getGroupLevelCode());
							}
						}
						
						if(!PROMOTION_TYPE_DOC_AMOUNT.contains(importNewVO.getType())) {
							if(groupLevelBuy.getSubGroupLevelProduct() != null && groupLevelBuy.getSubGroupLevelProduct().size() > 0) {
								importNewVO.setMessageError(R.getResource("ctkm.import.new.condition.doc.sub.condition", importNewVO.getType(), groupNewVO.getGroupCode(), groupLevelBuy.getGroupLevelCode()));
								return R.getResource("ctkm.import.new.condition.doc.sub.condition", importNewVO.getType(), groupNewVO.getGroupCode(), groupLevelBuy.getGroupLevelCode());
							}
						}						
					}
					sbMessage.append(validatePromotionGroupLevelConditionQuantityOrAmount(importNewVO, conditionType, groupNewVO));
					if(!StringUtil.isNullOrEmpty(sbMessage.toString())) {
						return sbMessage.toString();
					}					
				}
			}
		}
		return sbMessage.toString();
	}
	
	private String validatePromotionGroupSubCondition(PromotionImportVO importNewVO, Integer conditionType) {
		if(importNewVO.getProductGroups() != null && importNewVO.getProductGroups().size() > 0) {
			for(PromotionImportGroupVO groupNewVO : importNewVO.getProductGroups()) {
				if(groupNewVO.getGroupLevelBuys() != null && groupNewVO.getGroupLevelBuys().size() > 0) {
					int size = groupNewVO.getGroupLevelBuys().size();
					for(int i = 0; i < size; i++) {
						PromotionImportGroupLevelVO groupLevelBuy = groupNewVO.getGroupLevelBuys().get(i);
						if(groupLevelBuy.getSubGroupLevelProduct() == null || groupLevelBuy.getSubGroupLevelProduct().size() <= 0) {
							continue;
						}
						
						List<String> productCodes = new ArrayList<>();
						List<PromotionImportSubGroupLevelProductVO> lstSubGroupLevelProduct = groupLevelBuy.getSubGroupLevelProduct();
						for(int j = 0; j < lstSubGroupLevelProduct.size(); j++) {
							PromotionImportSubGroupLevelProductVO subGroupLevelProduct = lstSubGroupLevelProduct.get(j);
							if(CONDITION_TYPE_AMOUNT.equals(conditionType)) {
								if(subGroupLevelProduct.getAmount() == null || subGroupLevelProduct.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
									subGroupLevelProduct.setMessageError(R.getResource("ctkm.import.new.condition.group.sub.condition.quantity.invalid", importNewVO.getType(), groupNewVO.getGroupCode(), groupLevelBuy.getGroupLevelCode()));
									return R.getResource("ctkm.import.new.condition.group.sub.condition.quantity.invalid", importNewVO.getType(), groupNewVO.getGroupCode(), groupLevelBuy.getGroupLevelCode());
								}
								
								if(subGroupLevelProduct.getAmount().compareTo(groupLevelBuy.getAmount()) > 0) {
									subGroupLevelProduct.setMessageError(R.getResource("ctkm.import.new.condition.group.sub.condition.greater.amount", importNewVO.getType(), groupNewVO.getGroupCode(), groupLevelBuy.getGroupLevelCode()));
									return R.getResource("ctkm.import.new.condition.group.sub.condition.greater.amount", importNewVO.getType(), groupNewVO.getGroupCode(), groupLevelBuy.getGroupLevelCode());
								}
							} else {
								if(subGroupLevelProduct.getQuantity() == null || subGroupLevelProduct.getQuantity().compareTo(0) <= 0) {
									subGroupLevelProduct.setMessageError(R.getResource("ctkm.import.new.condition.group.sub.condition.quantity.invalid", importNewVO.getType(), groupNewVO.getGroupCode(), groupLevelBuy.getGroupLevelCode()));
									return R.getResource("ctkm.import.new.condition.group.sub.condition.quantity.invalid", importNewVO.getType(), groupNewVO.getGroupCode(), groupLevelBuy.getGroupLevelCode());
								}
								
								if(subGroupLevelProduct.getQuantity().compareTo(groupLevelBuy.getQuantity()) > 0) {
									subGroupLevelProduct.setMessageError(R.getResource("ctkm.import.new.condition.group.sub.condition.greater.quantity", importNewVO.getType(), groupNewVO.getGroupCode(), groupLevelBuy.getGroupLevelCode()));
									return R.getResource("ctkm.import.new.condition.group.sub.condition.greater.quantity", importNewVO.getType(), groupNewVO.getGroupCode(), groupLevelBuy.getGroupLevelCode());
								}
							}
							
							if(subGroupLevelProduct.getSubGroupLevelProductDetail() == null || subGroupLevelProduct.getSubGroupLevelProductDetail().size() <= 0) {
								subGroupLevelProduct.setMessageError(R.getResource("ctkm.import.new.condition.group.sub.condition.product.empty", importNewVO.getType(), groupNewVO.getGroupCode(), groupLevelBuy.getGroupLevelCode()));
								return R.getResource("ctkm.import.new.condition.group.sub.condition.product.empty", importNewVO.getType(), groupNewVO.getGroupCode(), groupLevelBuy.getGroupLevelCode());
							}
							
							if(subGroupLevelProduct.getSubGroupLevelProductDetail().size() < 2) {
								subGroupLevelProduct.setMessageError(R.getResource("ctkm.import.new.condition.group.sub.condition.product.size.invalid", importNewVO.getType(), groupNewVO.getGroupCode(), groupLevelBuy.getGroupLevelCode()));
								return R.getResource("ctkm.import.new.condition.group.sub.condition.product.size.invalid", importNewVO.getType(), groupNewVO.getGroupCode(), groupLevelBuy.getGroupLevelCode());
							}
							
							for(PromotionImportSubGroupLevelProductDetailVO product : subGroupLevelProduct.getSubGroupLevelProductDetail()) {
								if(productCodes.indexOf(product.getProductCode()) != -1) {
									subGroupLevelProduct.setMessageError(R.getResource("ctkm.import.new.condition.group.sub.condition.product.duplicate", importNewVO.getType(), groupNewVO.getGroupCode(), groupLevelBuy.getGroupLevelCode(), product.getProductCode()));
									return R.getResource("ctkm.import.new.condition.group.sub.condition.product.duplicate", importNewVO.getType(), groupNewVO.getGroupCode(), groupLevelBuy.getGroupLevelCode(), product.getProductCode());
								}
								productCodes.add(product.getProductCode());								
							}
						}
						
					}
				}
			}
		}
		return "";
	}
	
	private String validatePromotionDocSubCondition(PromotionImportVO importNewVO, Integer conditionType) {
		if(importNewVO.getProductGroups() != null && importNewVO.getProductGroups().size() > 0) {
			for(PromotionImportGroupVO groupNewVO : importNewVO.getProductGroups()) {			
				if(groupNewVO.getGroupLevelBuys() != null && groupNewVO.getGroupLevelBuys().size() > 0) {
					int size = groupNewVO.getGroupLevelBuys().size();
					List<String> rootProductCodes = new ArrayList<>();
					PromotionImportGroupLevelVO groupLevel = groupNewVO.getGroupLevelBuys().get(0);
					if(groupLevel.getSubGroupLevelProduct() != null && groupLevel.getSubGroupLevelProduct().size() > 0) {
						if(groupLevel.getSubGroupLevelProduct().size() > 1) {
							groupLevel.setMessageError(R.getResource("ctkm.import.new.condition.doc.sub.multi.group.level", importNewVO.getPromotionCode(), groupNewVO.getGroupCode(), groupLevel.getGroupLevelCode()));
							return R.getResource("ctkm.import.new.condition.doc.sub.multi.group.level", importNewVO.getPromotionCode(), groupNewVO.getGroupCode(), groupLevel.getGroupLevelCode());
						}
						
						PromotionImportSubGroupLevelProductVO subGroupLevelProduct = groupLevel.getSubGroupLevelProduct().get(0);
						if(subGroupLevelProduct.getSubGroupLevelProductDetail() == null && subGroupLevelProduct.getSubGroupLevelProductDetail().size() == 0) {
							groupLevel.setMessageError(R.getResource("ctkm.import.new.condition.doc.sub.group.level.not.product", importNewVO.getPromotionCode(), groupNewVO.getGroupCode(), groupLevel.getGroupLevelCode()));
							return R.getResource("ctkm.import.new.condition.doc.sub.group.level.not.product", importNewVO.getPromotionCode(), groupNewVO.getGroupCode(), groupLevel.getGroupLevelCode());
						}
						
						for(PromotionImportSubGroupLevelProductDetailVO subGroupLevelProductDetail : subGroupLevelProduct.getSubGroupLevelProductDetail()) {
							if (rootProductCodes.indexOf(subGroupLevelProductDetail.getProductCode()) == -1) {
								rootProductCodes.add(subGroupLevelProductDetail.getProductCode());
							}
						}
					}
					
					for(int i = 0; i < (size - 1); i++) {
						PromotionImportGroupLevelVO groupLevelBuyEx1 = groupNewVO.getGroupLevelBuys().get(i);						
						if((groupLevelBuyEx1.getSubGroupLevelProduct() == null || groupLevelBuyEx1.getSubGroupLevelProduct().size() == 0) && rootProductCodes.size() > 0) {
							groupLevelBuyEx1.setMessageError(R.getResource("ctkm.import.new.condition.product.is.not.homogeneous", importNewVO.getPromotionCode(), groupNewVO.getGroupCode(), groupLevelBuyEx1.getGroupLevelCode()));
							return R.getResource("ctkm.import.new.condition.product.is.not.homogeneous", importNewVO.getPromotionCode(), groupNewVO.getGroupCode(), groupLevelBuyEx1.getGroupLevelCode());
						}
						
						if(groupLevelBuyEx1.getSubGroupLevelProduct().size() > 1) {
							groupNewVO.getGroupLevelBuys().get(0).setMessageError(R.getResource("ctkm.import.new.condition.doc.sub.multi.group.level", importNewVO.getPromotionCode(), groupNewVO.getGroupCode(), groupLevelBuyEx1.getGroupLevelCode()));
							return R.getResource("ctkm.import.new.condition.doc.sub.multi.group.level", importNewVO.getPromotionCode(), groupNewVO.getGroupCode(), groupLevelBuyEx1.getGroupLevelCode());
						}	
						
						if(groupLevelBuyEx1.getSubGroupLevelProduct() != null && groupLevelBuyEx1.getSubGroupLevelProduct().size() > 0) {
							PromotionImportSubGroupLevelProductVO subGroupLevelProduct = groupLevelBuyEx1.getSubGroupLevelProduct().get(0);
							if(subGroupLevelProduct.getSubGroupLevelProductDetail() == null && subGroupLevelProduct.getSubGroupLevelProductDetail().size() == 0) {
								groupLevelBuyEx1.setMessageError(R.getResource("ctkm.import.new.condition.doc.sub.group.level.not.product", importNewVO.getPromotionCode(), groupNewVO.getGroupCode(), groupLevelBuyEx1.getGroupLevelCode()));
								return R.getResource("ctkm.import.new.condition.doc.sub.group.level.not.product", importNewVO.getPromotionCode(), groupNewVO.getGroupCode(), groupLevelBuyEx1.getGroupLevelCode());
							}
							
							List<String> lstProductCode = new ArrayList<>();
							for(PromotionImportSubGroupLevelProductDetailVO subGroupLevelProductDetail : subGroupLevelProduct.getSubGroupLevelProductDetail()) {
								if (lstProductCode.indexOf(subGroupLevelProductDetail.getProductCode()) == -1) {
									lstProductCode.add(subGroupLevelProductDetail.getProductCode());
								} else {
									groupLevelBuyEx1.setMessageError(R.getResource("ctkm.import.new.condition.product.is.duplicate", importNewVO.getPromotionCode(), groupNewVO.getGroupCode(), groupLevelBuyEx1.getGroupLevelCode()));
									return R.getResource("ctkm.import.new.condition.product.is.duplicate", importNewVO.getPromotionCode(), groupNewVO.getGroupCode(), groupLevelBuyEx1.getGroupLevelCode());
								}
							}
							
							if(lstProductCode.size() != rootProductCodes.size()) {
								groupLevelBuyEx1.setMessageError(R.getResource("ctkm.import.new.condition.product.is.not.homogeneous", importNewVO.getPromotionCode(), groupNewVO.getGroupCode()));
								return R.getResource("ctkm.import.new.condition.product.is.not.homogeneous", importNewVO.getPromotionCode(), groupNewVO.getGroupCode());
							}

							for(PromotionImportSubGroupLevelProductDetailVO subGroupLevelProductDetail : subGroupLevelProduct.getSubGroupLevelProductDetail()) {
								if(rootProductCodes.indexOf(subGroupLevelProductDetail.getProductCode()) == -1) {
									groupLevelBuyEx1.setMessageError(R.getResource("ctkm.import.new.condition.product.is.not.homogeneous", importNewVO.getPromotionCode(), groupNewVO.getGroupCode()));
									return R.getResource("ctkm.import.new.condition.product.is.not.homogeneous", importNewVO.getPromotionCode(), groupNewVO.getGroupCode());
								}
							}	
						}					
					}
				}
			}
		}
		return "";
	}
	
	private String validatePromotionKM(PromotionImportVO importNewVO, Integer conditionType) {
		StringBuilder sbMessage = new StringBuilder();
		if(importNewVO.getProductGroups() != null && importNewVO.getProductGroups().size() > 0) {
			sbMessage.append(validatePromotionMultiGroup(importNewVO));
			if(!StringUtil.isNullOrEmpty(sbMessage.toString())) {
				return sbMessage.toString();
			}
			for(PromotionImportGroupVO groupNewVO : importNewVO.getProductGroups()) {
				if(groupNewVO.getGroupLevelBuys() != null && groupNewVO.getGroupLevelBuys().size() > 0) {
					int size = groupNewVO.getGroupLevelKMs().size();
					for(int i = 0; i < size; i++) {					
						if(i < groupNewVO.getGroupLevelKMs().size()) {
							PromotionImportGroupLevelVO groupLevelKM = groupNewVO.getGroupLevelKMs().get(i);
							if(CONDITION_TYPE_AMOUNT.equals(conditionType)) {
								if(groupLevelKM.getAmount() == null) {
									groupLevelKM.setMessageError(R.getResource("ctkm.import.new.km.group.level.amount.null", importNewVO.getPromotionCode(), groupNewVO.getGroupCode(), groupLevelKM.getGroupLevelCode()));
									return R.getResource("ctkm.import.new.km.group.level.amount.null", importNewVO.getPromotionCode(), groupNewVO.getGroupCode(), groupLevelKM.getGroupLevelCode());
								}
								
								if(groupLevelKM.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
									groupLevelKM.setMessageError(R.getResource("ctkm.import.new.km.group.level.amount.zero", importNewVO.getPromotionCode(), groupNewVO.getGroupCode(), groupLevelKM.getGroupLevelCode()));
									return R.getResource("ctkm.import.new.km.group.level.amount.zero", importNewVO.getPromotionCode(), groupNewVO.getGroupCode(), groupLevelKM.getGroupLevelCode());
								}
							} else if(CONDITION_TYPE_PERCENT.equals(conditionType)) {
								if(groupLevelKM.getPercent() == null) {
									groupLevelKM.setMessageError(R.getResource("ctkm.import.new.km.group.level.percent.null", importNewVO.getPromotionCode(), groupNewVO.getGroupCode(), groupLevelKM.getGroupLevelCode()));
									return R.getResource("ctkm.import.new.km.group.level.percent.null", importNewVO.getPromotionCode(), groupNewVO.getGroupCode(), groupLevelKM.getGroupLevelCode());
								}
								if(groupLevelKM.getPercent() < 0) {
									groupLevelKM.setMessageError(R.getResource("ctkm.import.new.km.group.level.percent.zero", importNewVO.getPromotionCode(), groupNewVO.getGroupCode(), groupLevelKM.getGroupLevelCode()));
									return R.getResource("ctkm.import.new.km.group.level.percent.zero", importNewVO.getPromotionCode(), groupNewVO.getGroupCode(), groupLevelKM.getGroupLevelCode());
								}
								if(groupLevelKM.getPercent() > 100) {
									groupLevelKM.setMessageError(R.getResource("ctkm.import.new.km.group.level.percent.zero", importNewVO.getPromotionCode(), groupNewVO.getGroupCode(), groupLevelKM.getGroupLevelCode()));
									return R.getResource("ctkm.import.new.km.group.level.percent.zero", importNewVO.getPromotionCode(), groupNewVO.getGroupCode(), groupLevelKM.getGroupLevelCode());
								}								
							} else if(CONDITION_TYPE_QUANTITY.equals(conditionType)) {
								if(groupLevelKM.getGroupLevelProduct() != null && groupLevelKM.getGroupLevelProduct().size() > 0) {
									for(int j = 0; j < groupLevelKM.getGroupLevelProduct().size(); j++) {
										if(groupLevelKM.getGroupLevelProduct().get(j).getQuantity() == null) {
											groupLevelKM.getGroupLevelProduct().get(j).setMessageError(R.getResource("ctkm.import.new.km.group.level.product.quantity.null", importNewVO.getPromotionCode(), groupNewVO.getGroupCode(), groupLevelKM.getGroupLevelCode()));
											return R.getResource("ctkm.import.new.km.group.level.product.quantity.null", importNewVO.getPromotionCode(), groupNewVO.getGroupCode(), groupLevelKM.getGroupLevelCode());
										}
									}
								} else {
									groupLevelKM.setMessageError(R.getResource("ctkm.import.new.km.group.level.not.product", importNewVO.getPromotionCode(), groupNewVO.getGroupCode(), groupLevelKM.getGroupLevelCode()));
									return R.getResource("ctkm.import.new.km.group.level.not.product", importNewVO.getPromotionCode(), groupNewVO.getGroupCode(), groupLevelKM.getGroupLevelCode());
								}
							}
						}						
					}
				}
			}
		}
		return "";
	}
	
	private List<PromotionImportVO> validatePromotionImportNew (List<PromotionImportVO> importNewVOs, List<PromotionImportVO> promotionImportNewErrorVOs) {
		List<PromotionImportVO> promotionImportNewVOs = new ArrayList<>();
		for(PromotionImportVO importNewVO : importNewVOs) {
			String message = "";
			if(PromotionType.ZV01.getValue().equals(importNewVO.getType())) {
                message = validatePromotionImportZV01(importNewVO);
            } else if(PromotionType.ZV02.getValue().equals(importNewVO.getType())) {
                message = validatePromotionImportZV02(importNewVO);
            } else if(PromotionType.ZV03.getValue().equals(importNewVO.getType())) {
                message = validatePromotionImportZV03(importNewVO);
            } else if(PromotionType.ZV04.getValue().equals(importNewVO.getType())) {
                message = validatePromotionImportZV04(importNewVO);
            } else if(PromotionType.ZV05.getValue().equals(importNewVO.getType())) {
                message = validatePromotionImportZV05(importNewVO);
            } else if(PromotionType.ZV06.getValue().equals(importNewVO.getType())) {
                message = validatePromotionImportZV06(importNewVO);
            } else if(PromotionType.ZV07.getValue().equals(importNewVO.getType())) {
                message = validatePromotionImportZV07(importNewVO);
            } else if(PromotionType.ZV08.getValue().equals(importNewVO.getType())) {
                message = validatePromotionImportZV08(importNewVO);
            } else if(PromotionType.ZV09.getValue().equals(importNewVO.getType())) {
                message = validatePromotionImportZV09(importNewVO);
            } else if(PromotionType.ZV10.getValue().equals(importNewVO.getType())) {
                message = validatePromotionImportZV10(importNewVO);
            } else if(PromotionType.ZV11.getValue().equals(importNewVO.getType())) {
                message = validatePromotionImportZV11(importNewVO);
            } else if(PromotionType.ZV12.getValue().equals(importNewVO.getType())) {
                message = validatePromotionImportZV12(importNewVO);
            } else if(PromotionType.ZV13.getValue().equals(importNewVO.getType())) {
                message = validatePromotionImportZV13(importNewVO);
            } else if(PromotionType.ZV14.getValue().equals(importNewVO.getType())) {
                message = validatePromotionImportZV14(importNewVO);
            } else if(PromotionType.ZV15.getValue().equals(importNewVO.getType())) {
                message = validatePromotionImportZV15(importNewVO);
            } else if(PromotionType.ZV16.getValue().equals(importNewVO.getType())) {
                message = validatePromotionImportZV16(importNewVO);
            } else if(PromotionType.ZV17.getValue().equals(importNewVO.getType())) {
                message = validatePromotionImportZV17(importNewVO);
            } else if(PromotionType.ZV18.getValue().equals(importNewVO.getType())) {
                message = validatePromotionImportZV18(importNewVO);
            } else if(PromotionType.ZV19.getValue().equals(importNewVO.getType())) {
                message = validatePromotionImportZV19(importNewVO);
            } else if(PromotionType.ZV20.getValue().equals(importNewVO.getType())) {
                message = validatePromotionImportZV20(importNewVO);
            } else if(PromotionType.ZV21.getValue().equals(importNewVO.getType())) {
                message = validatePromotionImportZV21(importNewVO);
            } else if(PromotionType.ZV22.getValue().equals(importNewVO.getType())) {
                message = validatePromotionImportZV22(importNewVO);
            } else if(PromotionType.ZV23.getValue().equals(importNewVO.getType())) {
                message = validatePromotionImportZV23(importNewVO);
            }  else if(PromotionType.ZV24.getValue().equals(importNewVO.getType())) {
                message = validatePromotionImportZV24(importNewVO);
            }else {
                importNewVO.setMessageError(R.getResource("ctkm.import.new.program.type.not.process", importNewVO.getType()));
                promotionImportNewErrorVOs.add(importNewVO);
                continue;
            }
			
			if(!StringUtil.isNullOrEmpty(message)) {
				// lỗi CTKM
				promotionImportNewErrorVOs.add(importNewVO);
				continue;
			}
			
			promotionImportNewVOs.add(importNewVO);
		}
		return promotionImportNewVOs;
	}
	
	private String validatePromotionImportZV01(PromotionImportVO importNewVO) {
		resetProgramMultiple(importNewVO);
		resetProgramRecursive(importNewVO);
		resetProgramDiscountType(importNewVO);
		String messageError = validateVoucher(importNewVO);
		if(!StringUtil.isNullOrEmpty(messageError)) {
			return messageError;
		}
		
		messageError = validatePromotionLine(importNewVO, CONDITION_TYPE_QUANTITY);
		if(!StringUtil.isNullOrEmpty(messageError)) {
			return messageError;
		}
		
		messageError = validatePromotionKM(importNewVO, CONDITION_TYPE_PERCENT);
		if(!StringUtil.isNullOrEmpty(messageError)) {
			return messageError;
		}
		
		return "";
	}
	
	private String validatePromotionImportZV02(PromotionImportVO importNewVO) {
		resetProgramDiscountType(importNewVO);
		String messageError = validateVoucher(importNewVO);
		if(!StringUtil.isNullOrEmpty(messageError)) {
			return messageError;
		}
		
		messageError = validatePromotionLine(importNewVO, CONDITION_TYPE_QUANTITY);
		if(!StringUtil.isNullOrEmpty(messageError)) {
			return messageError;
		}
		
		messageError = validatePromotionKM(importNewVO, CONDITION_TYPE_AMOUNT);
		if(!StringUtil.isNullOrEmpty(messageError)) {
			return messageError;
		}
		return "";
	}
	
	private String validatePromotionImportZV03(PromotionImportVO importNewVO) {
		resetProgramDiscountType(importNewVO);
		resetProgramVoucherTime(importNewVO);
		
		String messageError = validatePromotionLine(importNewVO, CONDITION_TYPE_QUANTITY);
		if(!StringUtil.isNullOrEmpty(messageError)) {
			return messageError;
		}
		
		messageError = validatePromotionKM(importNewVO, CONDITION_TYPE_QUANTITY);
		if(!StringUtil.isNullOrEmpty(messageError)) {
			return messageError;
		}
		return "";
	}
	
	private String validatePromotionImportZV04(PromotionImportVO importNewVO) {
		resetProgramMultiple(importNewVO);
		resetProgramRecursive(importNewVO);
		resetProgramDiscountType(importNewVO);
		
		String messageError = validateVoucher(importNewVO);
		if(!StringUtil.isNullOrEmpty(messageError)) {
			return messageError;
		}
		
		messageError = validatePromotionLine(importNewVO, CONDITION_TYPE_AMOUNT);
		if(!StringUtil.isNullOrEmpty(messageError)) {
			return messageError;
		}
		
		messageError = validatePromotionKM(importNewVO, CONDITION_TYPE_PERCENT);
		if(!StringUtil.isNullOrEmpty(messageError)) {
			return messageError;
		}
		return "";
	}
	
	private String validatePromotionImportZV05(PromotionImportVO importNewVO) {
		resetProgramDiscountType(importNewVO);
		String messageError = validateVoucher(importNewVO);
		if(!StringUtil.isNullOrEmpty(messageError)) {
			return messageError;
		}
		
		messageError = validatePromotionLine(importNewVO, CONDITION_TYPE_AMOUNT);
		if(!StringUtil.isNullOrEmpty(messageError)) {
			return messageError;
		}
		
		messageError = validatePromotionKM(importNewVO, CONDITION_TYPE_AMOUNT);
		if(!StringUtil.isNullOrEmpty(messageError)) {
			return messageError;
		}
		return "";
	}
	
	private String validatePromotionImportZV06(PromotionImportVO importNewVO) {
		resetProgramDiscountType(importNewVO);
		resetProgramVoucherTime(importNewVO);
		
		String messageError = validatePromotionLine(importNewVO, CONDITION_TYPE_AMOUNT);
		if(!StringUtil.isNullOrEmpty(messageError)) {
			return messageError;
		}
		
		messageError = validatePromotionKM(importNewVO, CONDITION_TYPE_QUANTITY);
		if(!StringUtil.isNullOrEmpty(messageError)) {
			return messageError;
		}
		return "";
	}
	
	private String validatePromotionImportZV07(PromotionImportVO importNewVO) {
		resetProgramMultiple(importNewVO);
		resetProgramRecursive(importNewVO);
		resetProgramDiscountType(importNewVO);
		String messageError = validateVoucher(importNewVO);
		if(!StringUtil.isNullOrEmpty(messageError)) {
			return messageError;
		}
		
		messageError = validatePromotionGroup(importNewVO, CONDITION_TYPE_QUANTITY);
		if(!StringUtil.isNullOrEmpty(messageError)) {
			return messageError;
		}
		
		messageError = validatePromotionGroupSubCondition(importNewVO, CONDITION_TYPE_QUANTITY);
		if(!StringUtil.isNullOrEmpty(messageError)) {
			return messageError;
		}
		
		messageError = validatePromotionKM(importNewVO, CONDITION_TYPE_PERCENT);
		if(!StringUtil.isNullOrEmpty(messageError)) {
			return messageError;
		}
		return "";
	}
	
	private String validatePromotionImportZV08(PromotionImportVO importNewVO) {
		resetProgramDiscountType(importNewVO);
		String messageError = validateVoucher(importNewVO);
		if(!StringUtil.isNullOrEmpty(messageError)) {
			return messageError;
		}
		
		messageError = validatePromotionGroup(importNewVO, CONDITION_TYPE_QUANTITY);
		if(!StringUtil.isNullOrEmpty(messageError)) {
			return messageError;
		}
		
		messageError = validatePromotionGroupSubCondition(importNewVO, CONDITION_TYPE_QUANTITY);
		if(!StringUtil.isNullOrEmpty(messageError)) {
			return messageError;
		}
		
		messageError = validatePromotionKM(importNewVO, CONDITION_TYPE_AMOUNT);
		if(!StringUtil.isNullOrEmpty(messageError)) {
			return messageError;
		}
		return "";
	}
	
	private String validatePromotionImportZV09(PromotionImportVO importNewVO) {
		resetProgramDiscountType(importNewVO);
		resetProgramVoucherTime(importNewVO);
		
		String messageError = validatePromotionGroup(importNewVO, CONDITION_TYPE_QUANTITY);
		if(!StringUtil.isNullOrEmpty(messageError)) {
			return messageError;
		}
		
		messageError = validatePromotionGroupSubCondition(importNewVO, CONDITION_TYPE_QUANTITY);
		if(!StringUtil.isNullOrEmpty(messageError)) {
			return messageError;
		}
		
		messageError = validatePromotionKM(importNewVO, CONDITION_TYPE_QUANTITY);
		if(!StringUtil.isNullOrEmpty(messageError)) {
			return messageError;
		}
		return "";
	}
	
	private String validatePromotionImportZV10(PromotionImportVO importNewVO) {
		resetProgramRecursive(importNewVO);
		resetProgramDiscountType(importNewVO);
		
		String messageError = validateVoucher(importNewVO);
		if(!StringUtil.isNullOrEmpty(messageError)) {
			return messageError;
		}
		
		messageError = validatePromotionGroup(importNewVO, CONDITION_TYPE_AMOUNT);
		if(!StringUtil.isNullOrEmpty(messageError)) {
			return messageError;
		}
		
		messageError = validatePromotionGroupSubCondition(importNewVO, CONDITION_TYPE_AMOUNT);
		if(!StringUtil.isNullOrEmpty(messageError)) {
			return messageError;
		}
		
		messageError = validatePromotionKM(importNewVO, CONDITION_TYPE_PERCENT);
		if(!StringUtil.isNullOrEmpty(messageError)) {
			return messageError;
		}
		return "";
	}
	
	private String validatePromotionImportZV11(PromotionImportVO importNewVO) {
		resetProgramDiscountType(importNewVO);
		
		String messageError = validateVoucher(importNewVO);
		if(!StringUtil.isNullOrEmpty(messageError)) {
			return messageError;
		}
		
		messageError = validatePromotionGroup(importNewVO, CONDITION_TYPE_AMOUNT);
		if(!StringUtil.isNullOrEmpty(messageError)) {
			return messageError;
		}
		
		messageError = validatePromotionGroupSubCondition(importNewVO, CONDITION_TYPE_AMOUNT);
		if(!StringUtil.isNullOrEmpty(messageError)) {
			return messageError;
		}
		
		messageError = validatePromotionKM(importNewVO, CONDITION_TYPE_AMOUNT);
		if(!StringUtil.isNullOrEmpty(messageError)) {
			return messageError;
		}
		return "";
	}
	
	private String validatePromotionImportZV12(PromotionImportVO importNewVO) {
		resetProgramDiscountType(importNewVO);
		resetProgramVoucherTime(importNewVO);
		
		String messageError = validatePromotionGroup(importNewVO, CONDITION_TYPE_AMOUNT);
		if(!StringUtil.isNullOrEmpty(messageError)) {
			return messageError;
		}
		
		messageError = validatePromotionGroupSubCondition(importNewVO, CONDITION_TYPE_AMOUNT);
		if(!StringUtil.isNullOrEmpty(messageError)) {
			return messageError;
		}
		
		messageError = validatePromotionKM(importNewVO, CONDITION_TYPE_QUANTITY);
		if(!StringUtil.isNullOrEmpty(messageError)) {
			return messageError;
		}
		return "";
	}
	
	private String validatePromotionImportZV13(PromotionImportVO importNewVO) {
		resetProgramRecursive(importNewVO);
		resetProgramDiscountType(importNewVO);
		
		String messageError = validateVoucher(importNewVO);
		if(!StringUtil.isNullOrEmpty(messageError)) {
			return messageError;
		}
		
		messageError = validatePromotionBundle(importNewVO, CONDITION_TYPE_QUANTITY);
		if(!StringUtil.isNullOrEmpty(messageError)) {
			return messageError;
		}
		
		messageError = validatePromotionKM(importNewVO, CONDITION_TYPE_PERCENT);
		if(!StringUtil.isNullOrEmpty(messageError)) {
			return messageError;
		}
		return "";
	}
	
	private String validatePromotionImportZV14(PromotionImportVO importNewVO) {
		resetProgramDiscountType(importNewVO);
		
		String messageError = validateVoucher(importNewVO);
		if(!StringUtil.isNullOrEmpty(messageError)) {
			return messageError;
		}
		
		messageError = validatePromotionBundle(importNewVO, CONDITION_TYPE_QUANTITY);
		if(!StringUtil.isNullOrEmpty(messageError)) {
			return messageError;
		}
		
		messageError = validatePromotionKM(importNewVO, CONDITION_TYPE_AMOUNT);
		if(!StringUtil.isNullOrEmpty(messageError)) {
			return messageError;
		}
		return "";
	}
	
	private String validatePromotionImportZV15(PromotionImportVO importNewVO) {
		resetProgramDiscountType(importNewVO);
		resetProgramVoucherTime(importNewVO);
		
		String messageError = validatePromotionBundle(importNewVO, CONDITION_TYPE_QUANTITY);
		if(!StringUtil.isNullOrEmpty(messageError)) {
			return messageError;
		}
		
		messageError = validatePromotionKM(importNewVO, CONDITION_TYPE_QUANTITY);
		if(!StringUtil.isNullOrEmpty(messageError)) {
			return messageError;
		}
		return "";
	}
	
	private String validatePromotionImportZV16(PromotionImportVO importNewVO) {
		resetProgramRecursive(importNewVO);
		resetProgramDiscountType(importNewVO);
		
		String messageError = validateVoucher(importNewVO);
		if(!StringUtil.isNullOrEmpty(messageError)) {
			return messageError;
		}
		
		messageError = validatePromotionBundle(importNewVO, CONDITION_TYPE_AMOUNT);
		if(!StringUtil.isNullOrEmpty(messageError)) {
			return messageError;
		}
		
		messageError = validatePromotionKM(importNewVO, CONDITION_TYPE_PERCENT);
		if(!StringUtil.isNullOrEmpty(messageError)) {
			return messageError;
		}
		return "";
	}
	
	private String validatePromotionImportZV17(PromotionImportVO importNewVO) {
		resetProgramDiscountType(importNewVO);
		
		String messageError = validateVoucher(importNewVO);
		if(!StringUtil.isNullOrEmpty(messageError)) {
			return messageError;
		}
		
		messageError = validatePromotionBundle(importNewVO, CONDITION_TYPE_AMOUNT);
		if(!StringUtil.isNullOrEmpty(messageError)) {
			return messageError;
		}
		
		messageError = validatePromotionKM(importNewVO, CONDITION_TYPE_AMOUNT);
		if(!StringUtil.isNullOrEmpty(messageError)) {
			return messageError;
		}
		return "";
	}
		
	private String validatePromotionImportZV18(PromotionImportVO importNewVO) {
		resetProgramDiscountType(importNewVO);
		resetProgramVoucherTime(importNewVO);
		
		String messageError = validatePromotionBundle(importNewVO, CONDITION_TYPE_AMOUNT);
		if(!StringUtil.isNullOrEmpty(messageError)) {
			return messageError;
		}
		
		messageError = validatePromotionKM(importNewVO, CONDITION_TYPE_QUANTITY);
		if(!StringUtil.isNullOrEmpty(messageError)) {
			return messageError;
		}
		return "";
	}	
	
	private String validatePromotionImportZV19(PromotionImportVO importNewVO) {
		resetProgramMultiple(importNewVO);
		resetProgramRecursive(importNewVO);
		
		String messageError = validateVoucher(importNewVO);
		if(!StringUtil.isNullOrEmpty(messageError)) {
			return messageError;
		}
		
		messageError = validatePromotionDoc(importNewVO, CONDITION_TYPE_AMOUNT);
		if(!StringUtil.isNullOrEmpty(messageError)) {
			return messageError;
		}
		
		messageError = validatePromotionDocSubCondition(importNewVO, CONDITION_TYPE_AMOUNT);
		if(!StringUtil.isNullOrEmpty(messageError)) {
			return messageError;
		}
		
		messageError = validatePromotionKM(importNewVO, CONDITION_TYPE_PERCENT);
		if(!StringUtil.isNullOrEmpty(messageError)) {
			return messageError;
		}
		return "";
	}
	
	private String validatePromotionImportZV20(PromotionImportVO importNewVO) {
		String messageError = validateVoucher(importNewVO);
		if(!StringUtil.isNullOrEmpty(messageError)) {
			return messageError;
		}
		
		messageError = validatePromotionDoc(importNewVO, CONDITION_TYPE_AMOUNT);
		if(!StringUtil.isNullOrEmpty(messageError)) {
			return messageError;
		}
		
		messageError = validatePromotionDocSubCondition(importNewVO, CONDITION_TYPE_AMOUNT);
		if(!StringUtil.isNullOrEmpty(messageError)) {
			return messageError;
		}
		
		messageError = validatePromotionKM(importNewVO, CONDITION_TYPE_AMOUNT);
		if(!StringUtil.isNullOrEmpty(messageError)) {
			return messageError;
		}
		return "";
	}
	
	private String validatePromotionImportZV21(PromotionImportVO importNewVO) {
		resetProgramDiscountType(importNewVO);
		resetProgramVoucherTime(importNewVO);
		String messageError = validatePromotionDoc(importNewVO, CONDITION_TYPE_AMOUNT);
		if(!StringUtil.isNullOrEmpty(messageError)) {
			return messageError;
		}
		
		messageError = validatePromotionDocSubCondition(importNewVO, CONDITION_TYPE_AMOUNT);
		if(!StringUtil.isNullOrEmpty(messageError)) {
			return messageError;
		}
		
		messageError = validatePromotionKM(importNewVO, CONDITION_TYPE_QUANTITY);
		if(!StringUtil.isNullOrEmpty(messageError)) {
			return messageError;
		}
		return "";
	}
	
	private String validatePromotionImportZV22(PromotionImportVO importNewVO) {
		resetProgramMultiple(importNewVO);
		resetProgramRecursive(importNewVO);
		
		String messageError = validateVoucher(importNewVO);
		if(!StringUtil.isNullOrEmpty(messageError)) {
			return messageError;
		}
		
		messageError = validatePromotionDoc(importNewVO, CONDITION_TYPE_QUANTITY);
		if(!StringUtil.isNullOrEmpty(messageError)) {
			return messageError;
		}
		
		messageError = validatePromotionKM(importNewVO, CONDITION_TYPE_PERCENT);
		if(!StringUtil.isNullOrEmpty(messageError)) {
			return messageError;
		}
		return "";
	}
	
	private String validatePromotionImportZV23(PromotionImportVO importNewVO) {
		String messageError = validateVoucher(importNewVO);
		if(!StringUtil.isNullOrEmpty(messageError)) {
			return messageError;
		}
		
		messageError = validatePromotionDoc(importNewVO, CONDITION_TYPE_QUANTITY);
		if(!StringUtil.isNullOrEmpty(messageError)) {
			return messageError;
		}
		
		messageError = validatePromotionKM(importNewVO, CONDITION_TYPE_AMOUNT);
		if(!StringUtil.isNullOrEmpty(messageError)) {
			return messageError;
		}
		return "";
	}
	
	private String validatePromotionImportZV24(PromotionImportVO importNewVO) {
		resetProgramDiscountType(importNewVO);
		resetProgramVoucherTime(importNewVO);
		
		String messageError = validatePromotionDoc(importNewVO, CONDITION_TYPE_QUANTITY);
		if(!StringUtil.isNullOrEmpty(messageError)) {
			return messageError;
		}
		
		messageError = validatePromotionKM(importNewVO, CONDITION_TYPE_QUANTITY);
		if(!StringUtil.isNullOrEmpty(messageError)) {
			return messageError;
		}
		return "";
	}

	private List<PromotionImportVO> sortPromotionImportNew (List<PromotionImportVO> importNewVOs) {
		// danh index cho muc theo index của list groupLevel và sort mức
		for(PromotionImportVO importNewVO : importNewVOs) {
			for(PromotionImportGroupVO groupNewVO : importNewVO.getProductGroups()) {
				List<PromotionImportGroupLevelVO> groupLevelBuys = groupNewVO.getGroupLevelBuys();
				for(int i = 0; i < groupLevelBuys.size(); i++) {
					groupLevelBuys.get(i).setIndex(i + 1);
				}				
			}
		}
		
		// sort mức mua
		List<String> lstZVQuantity = PromotionProgramMgr.PROMOTION_TYPE_QUANTITY_CONDITIONS;
		List<String> lstZVsmount = PromotionProgramMgr.PROMOTION_TYPE_AMOUNT_CONDITIONS;
		List<String> lstZVLine = PromotionProgramMgr.PROMOTION_TYPE_LINES;
		List<String> lstZVGroup = PromotionProgramMgr.PROMOTION_TYPE_GROUPS;
		List<String> lstZVbundle = PromotionProgramMgr.PROMOTION_TYPE_GROUPS;
		List<String> lstZVDoc = PromotionProgramMgr.PROMOTION_TYPE_DOCS;
		for(PromotionImportVO importNewVO : importNewVOs) {
			for(PromotionImportGroupVO groupNewVO : importNewVO.getProductGroups()) {
				List<PromotionImportGroupLevelVO> groupLevelBuys = groupNewVO.getGroupLevelBuys();
				if(lstZVGroup.contains(importNewVO.getType()) || lstZVDoc.contains(importNewVO.getType())) {
					if(lstZVQuantity.contains(importNewVO.getType())) {
						Collections.sort(groupLevelBuys, new Comparator<PromotionImportGroupLevelVO>() {
							@Override
							public int compare(PromotionImportGroupLevelVO arr1, PromotionImportGroupLevelVO arr2) {
								if(arr1.getQuantity() > arr2.getQuantity()) {
									return -1;
								}
								
								if(arr1.getQuantity() < arr2.getQuantity()) {
									return 1;
								}
								return 0;
							}							
						});	
					} else if(lstZVsmount.contains(importNewVO.getType())) {
						Collections.sort(groupLevelBuys, new Comparator<PromotionImportGroupLevelVO>() {
							@Override
							public int compare(PromotionImportGroupLevelVO arr1, PromotionImportGroupLevelVO arr2) {
								if(arr1.getAmount().compareTo(arr2.getAmount()) == 1) {
									return -1;
								}
								
								if(arr1.getAmount().compareTo(arr2.getAmount()) == -1) {
									return 1;
								}
								return 0;
							}							
						});	
					}
				} else if(lstZVLine.contains(importNewVO.getType()) || lstZVbundle.contains(importNewVO.getType())) {
					if(lstZVQuantity.contains(importNewVO.getType())) {
						Collections.sort(groupLevelBuys, new Comparator<PromotionImportGroupLevelVO>() {
							@Override
							public int compare(PromotionImportGroupLevelVO arr1, PromotionImportGroupLevelVO arr2) {
								if(arr1.getGroupLevelProduct().get(0).getQuantity() > arr2.getGroupLevelProduct().get(0).getQuantity()) {
									return -1;
								}
								
								if(arr1.getGroupLevelProduct().get(0).getQuantity() < arr2.getGroupLevelProduct().get(0).getQuantity()) {
									return 1;
								}
								return 0;
							}							
						});	
					} else if(lstZVsmount.contains(importNewVO.getType())) {
						Collections.sort(groupLevelBuys, new Comparator<PromotionImportGroupLevelVO>() {
							@Override
							public int compare(PromotionImportGroupLevelVO arr1, PromotionImportGroupLevelVO arr2) {
								if(arr1.getGroupLevelProduct().get(0).getAmount().compareTo(arr2.getGroupLevelProduct().get(0).getAmount()) == 1) {
									return -1;
								}
								
								if(arr1.getGroupLevelProduct().get(0).getAmount().compareTo(arr2.getGroupLevelProduct().get(0).getAmount()) == -1) {
									return 1;
								}
								return 0;
							}							
						});	
					}
				}					
			}
		}		
		
		// sort mức khuyen mãi theo mức mua
		for(PromotionImportVO importNewVO : importNewVOs) {
			for(PromotionImportGroupVO groupNewVO : importNewVO.getProductGroups()) {
				List<PromotionImportGroupLevelVO> groupLevelBuys = groupNewVO.getGroupLevelBuys();
				List<PromotionImportGroupLevelVO> tmpGroupLevelKMs = groupNewVO.getGroupLevelKMs();
				List<PromotionImportGroupLevelVO> groupLevelKMs = new ArrayList<>();
				for(int i = 0; i < groupLevelBuys.size(); i++) {
					groupLevelKMs.add(tmpGroupLevelKMs.get(groupLevelBuys.get(i).getIndex() - 1));
				}
				groupNewVO.setGroupLevelKMs(groupLevelKMs);
			}
		}
		return importNewVOs;
	}
	
	private String WriteFileErrorNew (List<CellBean> infoPromotionError, List<CellBean> infoPromotionDetailError, List<CellBean> infoPromotionShopError) {
		final String serverPath = ServletActionContext.getServletContext().getRealPath("/");
		String templateName = "Bieu_mau_thong_tin_CTKM_Error.xls";
		String templateFileName = serverPath + Configuration.getExcelTemplatePathCatalog()  + templateName;
		String outputName = DateUtil.toDateString(DateUtil.now(), DateUtil.DATE_FORMAT_EXCEL_FILE) + "_" + templateName;
		templateFileName = templateFileName.replace('/', File.separatorChar);
		String outstoredFileName = Configuration. getStoreImportDownloadPath()+ outputName;
		outstoredFileName = outstoredFileName.replace('/', File.separatorChar);
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("report1", infoPromotionError);
		params.put("report2", infoPromotionDetailError);
		params.put("report3", infoPromotionShopError);
		fileNameFail = Configuration.getStoreImportFailDownloadPath()+ outputName;
		XLSTransformer transformer = new XLSTransformer();
		try {
			transformer.transformXLS(templateFileName, params, outstoredFileName);
		} catch (ParsePropertyException e) {
			LogUtility.logError(e, e.getMessage());
		} catch (InvalidFormatException e) {
			LogUtility.logError(e, e.getMessage());
		} catch (IOException e) {
			LogUtility.logError(e, e.getMessage());
		}
		return SUCCESS;
	}

	private String getStringValueForCell(Object value) {
		if(value instanceof String) {
			if(!StringUtil.isNullOrEmpty(value.toString())) {
				return value.toString().trim();
			}
		}	
			
		if(value instanceof Date) {
			if(!StringUtil.isNullOrEmpty(String.valueOf(value))) {
				return DateUtil.toDateString((Date)value, DateUtil.DATE_FORMAT_DDMMYYYY);
			}
		}
		
		if(value instanceof BigDecimal) {
			if(value != null) {
				return String.valueOf(value);
			}
		}
		return "";
	}
	
	private void convertObjectPromotionToCellBeanNew(List<PromotionImportVO> importNewVOs, List<CellBean> infoPromotionError, List<CellBean> infoPromotionDetailError, List<CellBean> infoPromotionShopError) {
		for (PromotionImportVO importNewVO : importNewVOs) {
			CellBean promotionInfoRow = new CellBean();
			promotionInfoRow.setContent1(getStringValueForCell(importNewVO.getPromotionCode()));
			promotionInfoRow.setContent2(getStringValueForCell(importNewVO.getPromotionName()));
			promotionInfoRow.setContent3(getStringValueForCell(importNewVO.getVersion()));
			promotionInfoRow.setContent4(getStringValueForCell(importNewVO.getType()));
			promotionInfoRow.setContent5(getStringValueForCell(importNewVO.getFromDate()));
			promotionInfoRow.setContent6(getStringValueForCell(importNewVO.getToDate()));
			promotionInfoRow.setContent7(getStringValueForCell(importNewVO.getNotice()));
			promotionInfoRow.setContent8(getStringValueForCell(importNewVO.getDescriptionProduct()));
			promotionInfoRow.setContent9(getStringValueForCell(importNewVO.getDescription()));
			promotionInfoRow.setContent10(getStringValueForCell(importNewVO.isMultiple()));
			promotionInfoRow.setContent11(!StringUtil.isNullOrEmpty(String.valueOf(importNewVO.isRecursive())) ? String.valueOf(importNewVO.isRecursive() ? 1 : 0):"");
			promotionInfoRow.setContent12(importNewVO.getRewardType()!=null ? String.valueOf(importNewVO.getRewardType()):"");
			promotionInfoRow.setContent13(getStringValueForCell(importNewVO.getApplyFromDate()));
			promotionInfoRow.setContent14(getStringValueForCell(importNewVO.getApplyToDate()));
			promotionInfoRow.setContent15(getStringValueForCell(importNewVO.getDiscountType()));
			promotionInfoRow.setErrMsg(importNewVO.getMessageError());
			infoPromotionError.add(promotionInfoRow);
			
			for(PromotionImportShopVO importShopNewVO : importNewVO.getShops()) {
				CellBean promotionShop = new CellBean();
				promotionShop.setContent1(getStringValueForCell(importNewVO.getPromotionCode()));
				promotionShop.setContent2(getStringValueForCell(importShopNewVO.getShopCode()));
				promotionShop.setContent3(getStringValueForCell(importShopNewVO.getNum()));
				promotionShop.setContent4(importShopNewVO.getAmount() != null ? StringUtil.convertMoney(importShopNewVO.getAmount()):"");
				promotionShop.setContent5(getStringValueForCell(importShopNewVO.getQuantity()));
				promotionShop.setContent6(importShopNewVO.getMessageError());
				if(!StringUtil.isNullOrEmpty(importShopNewVO.getKeyMessage())) {
					promotionShop.setErrMsg(R.getResource(importShopNewVO.getKeyMessage()));
				}
				
				infoPromotionShopError.add(promotionShop);
			}
			for(PromotionImportGroupVO groupNewVO : importNewVO.getProductGroups()) {				
				for(int i = 0; i < groupNewVO.getGroupLevelBuys().size(); i++) {
					PromotionImportGroupLevelVO groupLevelNewMuaVO = groupNewVO.getGroupLevelBuys().get(i);
					PromotionImportGroupLevelVO groupLevelNewKMVO = groupNewVO.getGroupLevelKMs().get(i);
					int index = infoPromotionDetailError.size();//13 - > 23
					// ghi muc mua
					CellBean promotionDetailGroupLevel = new CellBean();
					promotionDetailGroupLevel.setContent1(getStringValueForCell(importNewVO.getPromotionCode()));
					promotionDetailGroupLevel.setContent2(importNewVO.getType());
					promotionDetailGroupLevel.setContent3(groupNewVO.getGroupCode());
					promotionDetailGroupLevel.setContent4(groupLevelNewMuaVO.getGroupLevelCode());
					promotionDetailGroupLevel.setContent5("X");
					promotionDetailGroupLevel.setContent8(groupLevelNewMuaVO.getQuantity()!=null?String.valueOf(groupLevelNewMuaVO.getQuantity()):"");
					promotionDetailGroupLevel.setContent9(!StringUtil.isNullOrEmpty(String.valueOf(groupLevelNewMuaVO.getUnit())) ? groupLevelNewMuaVO.getUnit()==1 ? R.getResource("ctkm.import.new.le"):R.getResource("ctkm.import.new.thung"):"");
					promotionDetailGroupLevel.setContent10(groupLevelNewMuaVO.getAmount() != null ? StringUtil.convertMoney(groupLevelNewMuaVO.getAmount()):"");
					promotionDetailGroupLevel.setContent12(groupLevelNewKMVO.getAmount() != null ? StringUtil.convertMoney(groupLevelNewKMVO.getAmount()):"");
					promotionDetailGroupLevel.setContent13(groupLevelNewKMVO.getPercent() != null ? String.valueOf(groupLevelNewKMVO.getPercent()):"");
					promotionDetailGroupLevel.setErrMsg(getStringValueForCell(groupLevelNewMuaVO.getMessageError()) + getStringValueForCell(groupLevelNewKMVO.getMessageError())
							+ groupNewVO.getMessageError());
					infoPromotionDetailError.add(promotionDetailGroupLevel);
					for (int j = 0; j < groupLevelNewMuaVO.getGroupLevelProduct().size(); j++) {						
						// cell con
						CellBean promotionDetailGroupLevelproduct = new CellBean();
						promotionDetailGroupLevelproduct.setContent1(getStringValueForCell(importNewVO.getPromotionCode()));
						promotionDetailGroupLevelproduct.setContent2(importNewVO.getType());
						promotionDetailGroupLevelproduct.setContent3(groupNewVO.getGroupCode());
						promotionDetailGroupLevelproduct.setContent4(groupLevelNewMuaVO.getGroupLevelCode());
						promotionDetailGroupLevelproduct.setContent5("X");
						promotionDetailGroupLevelproduct.setContent7(getStringValueForCell(groupLevelNewMuaVO.getGroupLevelProduct().get(j).getProductCode()));
						promotionDetailGroupLevelproduct.setContent10(groupLevelNewMuaVO.getGroupLevelProduct().get(j).getAmount() != null ? StringUtil.convertMoney(groupLevelNewMuaVO.getGroupLevelProduct().get(j).getAmount()):"");
						promotionDetailGroupLevelproduct.setContent11(groupLevelNewMuaVO.getGroupLevelProduct().get(j).getQuantity()!=null?String.valueOf(groupLevelNewMuaVO.getGroupLevelProduct().get(j).getQuantity()):"");
						promotionDetailGroupLevelproduct.setErrMsg(getStringValueForCell(groupLevelNewMuaVO.getGroupLevelProduct().get(j).getMessageError()));
						infoPromotionDetailError.add(promotionDetailGroupLevelproduct);
					}
					
					for (int j = 0; j < groupLevelNewMuaVO.getSubGroupLevelProduct().size(); j++) {
						PromotionImportSubGroupLevelProductVO subGroupLevelProduct = groupLevelNewMuaVO.getSubGroupLevelProduct().get(j);
						//cell tong nhom dieu kien con
						CellBean promotionDetailSubGroupLevel = new CellBean();
						promotionDetailSubGroupLevel.setContent1(getStringValueForCell(importNewVO.getPromotionCode()));
						promotionDetailSubGroupLevel.setContent2(importNewVO.getType());
						promotionDetailSubGroupLevel.setContent3(groupNewVO.getGroupCode());
						promotionDetailSubGroupLevel.setContent4(groupLevelNewMuaVO.getGroupLevelCode());
						promotionDetailSubGroupLevel.setContent6("X");
						promotionDetailSubGroupLevel.setContent8(groupLevelNewMuaVO.getSubGroupLevelProduct().get(j).getQuantity()!=null?String.valueOf(groupLevelNewMuaVO.getSubGroupLevelProduct().get(j).getQuantity()):"");
						promotionDetailSubGroupLevel.setContent9(!StringUtil.isNullOrEmpty(String.valueOf(groupLevelNewMuaVO.getUnit())) ? groupLevelNewMuaVO.getUnit()==1 ? R.getResource("ctkm.import.new.le"):R.getResource("ctkm.import.new.thung"):"");
						promotionDetailSubGroupLevel.setContent10(groupLevelNewMuaVO.getSubGroupLevelProduct().get(j).getAmount() != null ? StringUtil.convertMoney(groupLevelNewMuaVO.getSubGroupLevelProduct().get(j).getAmount()):"");
						promotionDetailSubGroupLevel.setErrMsg(getStringValueForCell(groupLevelNewMuaVO.getSubGroupLevelProduct().get(j).getMessageError()));
						infoPromotionDetailError.add(promotionDetailSubGroupLevel);
						for(PromotionImportSubGroupLevelProductDetailVO product : subGroupLevelProduct.getSubGroupLevelProductDetail()) {
							// cell chi tiet cho nhom dieu kien con
							CellBean promotionDetailSubGroupLevelproduct = new CellBean();
							promotionDetailSubGroupLevelproduct.setContent1(getStringValueForCell(importNewVO.getPromotionCode()));
							promotionDetailSubGroupLevelproduct.setContent2(importNewVO.getType());
							promotionDetailSubGroupLevelproduct.setContent3(groupNewVO.getGroupCode());
							promotionDetailSubGroupLevelproduct.setContent4(groupLevelNewMuaVO.getGroupLevelCode());
							promotionDetailSubGroupLevelproduct.setContent6("X");
							promotionDetailSubGroupLevelproduct.setContent7(getStringValueForCell(product.getProductCode()));
							promotionDetailSubGroupLevelproduct.setErrMsg(getStringValueForCell(product.getMessageError()));
							infoPromotionDetailError.add(promotionDetailSubGroupLevelproduct);
						}
					}
					
					for (int j = 0; j < groupLevelNewKMVO.getGroupLevelProduct().size(); j++) {
						int indexGroupKM = index + j;
						CellBean promotionDetailSubGroupLevelproduct = null;
						if(indexGroupKM < infoPromotionDetailError.size()) {
							promotionDetailSubGroupLevelproduct = infoPromotionDetailError.get(indexGroupKM);
						}
						
						if(promotionDetailSubGroupLevelproduct == null) {
							promotionDetailSubGroupLevelproduct = new CellBean();
							infoPromotionDetailError.add(promotionDetailSubGroupLevelproduct);
						}
						
						// set thong tin cho nhom KM
						promotionDetailSubGroupLevelproduct.setContent12(groupLevelNewKMVO.getAmount() != null ? StringUtil.convertMoney(groupLevelNewKMVO.getAmount()):"");
						promotionDetailSubGroupLevelproduct.setContent13(groupLevelNewKMVO.getPercent() != null ? String.valueOf(groupLevelNewKMVO.getPercent()):"");
						promotionDetailSubGroupLevelproduct.setContent14(getStringValueForCell(groupLevelNewKMVO.getGroupLevelProduct().get(j).getProductCode()));
						promotionDetailSubGroupLevelproduct.setContent15(groupLevelNewKMVO.getGroupLevelProduct().get(j).getQuantity()!=null?String.valueOf(groupLevelNewKMVO.getGroupLevelProduct().get(j).getQuantity()):"");
						promotionDetailSubGroupLevelproduct.setContent16(!StringUtil.isNullOrEmpty(String.valueOf(groupLevelNewKMVO.getUnit())) ? groupLevelNewKMVO.getUnit()==1 ? R.getResource("ctkm.import.new.le"):R.getResource("ctkm.import.new.thung"):"");
						promotionDetailSubGroupLevelproduct.setContent17(groupLevelNewKMVO.getGroupLevelProduct().get(j).isRequired() ? "x" : "");
						if (StringUtil.isNullOrEmpty(promotionDetailSubGroupLevelproduct.getErrMsg())){
							promotionDetailSubGroupLevelproduct.setErrMsg(groupLevelNewKMVO.getGroupLevelProduct().get(j).getMessageError());	
						}else{
							promotionDetailSubGroupLevelproduct.setErrMsg(promotionDetailSubGroupLevelproduct.getErrMsg() + getStringValueForCell(groupLevelNewKMVO.getGroupLevelProduct().get(j).getMessageError()));
						}
					}
				}
			}
			
		}
	}

	/**
	 * end import 24zv new
	 * */
	
	
	private String importExcelPromotionNew() {
		try{
			List<List<String>> infoPromotion = new ArrayList<>();
			List<List<String>> infoPromotionDetail = new ArrayList<>();
			List<List<String>> infoPromotionShop = new ArrayList<>();
			
			List<CellBean> infoPromotionError = new ArrayList<>();
			List<CellBean> infoPromotionDetailError = new ArrayList<>();
			List<CellBean> infoPromotionShopError = new ArrayList<>();
			List<PromotionImportNewVO> promotionImportNewErrorVOs = null;
			
			getDataImportExcelPromotion(infoPromotion, infoPromotionDetail, infoPromotionShop, infoPromotionError, infoPromotionDetailError, infoPromotionShopError);
			// xu ly xuất lỗi
			if (infoPromotionError.size() > 0 || infoPromotionDetailError.size() > 0 || infoPromotionShopError.size() > 0) {
				return WriteFileError(infoPromotionError, infoPromotionDetailError, infoPromotionShopError);
			}
			
			promotionImportNewErrorVOs = new ArrayList<>();
			List<PromotionImportNewVO> promotionImportNewVOs = convertDataImportExcelPromotion(infoPromotion, infoPromotionDetail, infoPromotionShop, infoPromotionError, infoPromotionDetailError, infoPromotionShopError);
			if(promotionImportNewVOs != null && promotionImportNewVOs.size() > 0) {
				// bỏ những CT k hợp le và những CT nằm ngoài các ZV cần xử lý
				promotionImportNewVOs = validatePromotionImport(promotionImportNewVOs, promotionImportNewErrorVOs);
			}
			// sap xep lại cac mức cho CTKM
			promotionImportNewVOs = sortPromotionImport(promotionImportNewVOs);
			//save
			totalItem = promotionImportNewErrorVOs.size() + promotionImportNewVOs.size();
			numFail = promotionImportNewErrorVOs.size();
			if(promotionImportNewVOs != null && promotionImportNewVOs.size() > 0) {
				promotionImportNewErrorVOs = promotionProgramMgr.saveImportPromotionNew(promotionImportNewVOs, promotionImportNewErrorVOs, getLogInfoVO());
				// thông tin tra ve
				numFail = promotionImportNewErrorVOs.size();
				for (PromotionImportNewVO promotion : promotionImportNewVOs) {
					PromotionProgram pp = promotionProgramMgr.getPromotionProgramByCode(promotion.getPromotionCode());
					if (pp != null) {
						promotionProgramMgr.updateMD5ValidCode(pp, getLogInfoVO());
					}
				}
			}
			
			// xu ly nêu có loi
			if (promotionImportNewErrorVOs.size() > 0) {
				convertObjectPromotionToCellBean(promotionImportNewErrorVOs, infoPromotionError, infoPromotionDetailError, infoPromotionShopError);
				if (infoPromotionError.size() > 0 || infoPromotionDetailError.size() > 0 || infoPromotionShopError.size() > 0) {
					return WriteFileError(infoPromotionError, infoPromotionDetailError, infoPromotionShopError);
				}
			}
		} catch(Exception ex) {
			errMsg = Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "system.error");
			LogUtility.logErrorStandard(ex, R.getResource("web.log.message.error", "vnm.web.action.program.PromotionCatalogAction.importExcelPromotionNew"), createLogErrorStandard(actionStartTime));
		}		
		return SUCCESS;
	}
		
	private String WriteFileError(List<CellBean> infoPromotionError, List<CellBean> infoPromotionDetailError, List<CellBean> infoPromotionShopError){
		final String serverPath = ServletActionContext.getServletContext().getRealPath("/");
		String templateName = "Bieu_mau_thong_tin_CTKM_New_Error.xls";
		String templateFileName = serverPath + Configuration.getExcelTemplatePathCatalog()  + templateName;
		String outputName = DateUtil.toDateString(DateUtil.now(), DateUtil.DATE_FORMAT_EXCEL_FILE) + "_" + templateName;
		templateFileName = templateFileName.replace('/', File.separatorChar);
		String outstoredFileName = Configuration. getStoreImportDownloadPath()+ outputName;
		outstoredFileName = outstoredFileName.replace('/', File.separatorChar);
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("report1", infoPromotionError);
		params.put("report2", infoPromotionDetailError);
		params.put("report3", infoPromotionShopError);
		//this.outputName = outputName;
		fileNameFail = Configuration.getStoreImportFailDownloadPath()+ outputName;
		XLSTransformer transformer = new XLSTransformer();
		try {
			transformer.transformXLS(templateFileName, params, outstoredFileName);
		} catch (ParsePropertyException e) {
			LogUtility.logError(e, e.getMessage());
		} catch (InvalidFormatException e) {
			LogUtility.logError(e, e.getMessage());
		} catch (IOException e) {
			LogUtility.logError(e, e.getMessage());
		}
		return SUCCESS;
	}
		
	private void getDataImportExcelPromotion (List<List<String>> infoPromotion, List<List<String>> infoPromotionDetail, List<List<String>> infoPromotionShop, List<CellBean> infoPromotionError, List<CellBean> infoPromotionDetailError, List<CellBean> infoPromotionShopError) {
		InputStream is = null;
		Workbook promotionWorkBook = null;
		Date fromDate = null;
		Date toDate = null;
		boolean isContinue = true;
		String errMsg = "";
		DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		dateFormat.setLenient(false);
		final int NUM_COL_PROMOTION_SHEET = 14;
		final int NUM_COL_PROMOTION_DETAIL_SHEET = 17;
		final int NUM_COL_PROMOTION_SHOP_SHEET = 5;
		final List<String> PROMOTION_TYPES = Arrays.asList("ZV07","ZV08","ZV09","ZV10","ZV11","ZV12",
														   "ZV19","ZV20","ZV21");
		final List<String> FREE_ITEMS_REWARD_TYPES = Arrays.asList("ZV09","ZV12","ZV21");
		final List<String> UNIT = Arrays.asList(R.getResource("ctkm.import.new.le"), R.getResource("ctkm.import.new.thung"));
		List<String> promotionProgramCodes = new ArrayList<>();
		List<String> programAndTypeCodes = new ArrayList<>();
			try {
				is = new FileInputStream(excelFile);
				if (!is.markSupported()) {
					is = new PushbackInputStream(is, 8);
				}
				if (POIFSFileSystem.hasPOIFSHeader(is)) {
					promotionWorkBook = new HSSFWorkbook(is);
				} else if (POIXMLDocument.hasOOXMLHeader(is)) {
					promotionWorkBook = new XSSFWorkbook(OPCPackage.open(is));
				}
				if (promotionWorkBook != null) {
					Sheet promotionSheet = promotionWorkBook.getSheetAt(0);
					Sheet promotionDetailSheet = promotionWorkBook.getSheetAt(1);
					Sheet promotionShopSheet = promotionWorkBook.getSheetAt(2);
					if(promotionSheet!=null)
					{
						//doc sheet THONG TIN CHUNG
						Iterator<?> rowIter = promotionSheet.rowIterator();
						rowIter.next();
						while (rowIter.hasNext()) {

							Row currentRow = (Row) rowIter.next();
							isContinue = true;
							for(int i=0;i<NUM_COL_PROMOTION_SHEET;i++)
							{
								if (currentRow.getCell(i) != null && !StringUtil.isNullOrEmpty(getCellValueToString(currentRow.getCell(i)))) {
									isContinue = false;
									break;
								}
							}
							if(isContinue) continue;
							String[] data = new String[NUM_COL_PROMOTION_SHEET];
							//MA CHUONG TRINH KHUYEN MAI
							data[0] = getCellValueToString(currentRow.getCell(0));
							//TEN CHUONG TRINH KHUYEN MAI
							data[1] = getCellValueToString(currentRow.getCell(1));
							//PHIEN BAN
							data[2] = getCellValueToString(currentRow.getCell(2));
							//LOAI CHUONG TRINH KHUYEN MAI
							data[3] = getCellValueToString(currentRow.getCell(3));
							//TU NGAY
							data[4] = getCellValueToString(currentRow.getCell(4));
							//DEN NGAY
							data[5] = getCellValueToString(currentRow.getCell(5));
							//SO THONG BAO
							data[6] = getCellValueToString(currentRow.getCell(6));
							//TEN NHOM / SAN PHAM
							data[7] = getCellValueToString(currentRow.getCell(7));
							//MO TA CHUONG TRINH 
							data[8] = getCellValueToString(currentRow.getCell(8));
							//BOI SO
							data[9] = getCellValueToString(currentRow.getCell(9));
							//TOI UU
							data[10] = getCellValueToString(currentRow.getCell(10));
							//LOAI TRA THUONG
							data[11] = getCellValueToString(currentRow.getCell(11));
							//TU NGAY TRA THUONG
							data[12] = getCellValueToString(currentRow.getCell(12));
							//DEN NGAY TRA THUONG
							data[13] = getCellValueToString(currentRow.getCell(13));
							for(int i=0;i<NUM_COL_PROMOTION_SHEET;i++)
							{
								data[i] = data[i].trim();
							}
							
							List<String> rowData = Arrays.asList(data);
							errMsg = "";
							//Kiem tra Ma Chuong trinh
							String promotionCode  = rowData.get(0).toUpperCase();
							if (promotionCode.isEmpty()) {
								errMsg += R.getResource("catalog.promotion.import.column.null", "Mã CTKM");
							} else {
								errMsg += ValidateUtil.validateField(promotionCode, "catalog.promotion.import.column.progcode", 50, ConstantManager.ERR_REQUIRE, ConstantManager.ERR_MAX_LENGTH, ConstantManager.ERR_EXIST_SPECIAL_CHAR_IN_CODE);
							}
							boolean isDuplicated = false;
							for(int i = 0;i<infoPromotion.size();i++)
							{
								if(promotionCode==infoPromotion.get(i).get(0))
								{
									isDuplicated = true;
									break;
								}
							}
							
							if (isDuplicated) {
								errMsg += Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "promotion.product.import.duplicate", rowData);
								errMsg += "\n";
							}
							PromotionProgram existPromotion = null;
							try {
								existPromotion = promotionProgramMgr.getPromotionProgramByCode(promotionCode);
							} catch (BusinessException e) {
								// TODO Auto-generated catch block
								LogUtility.logErrorStandard(e, R.getResource("web.log.message.error", "vnm.web.action.program.PromotionCatalogAction.getDataImportExcelPromotion()"), createLogErrorStandard(commonMgr.getSysDate()));
							}
							if (existPromotion != null && !ActiveType.WAITING.equals(existPromotion.getStatus())) {
								errMsg += Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "catalog.promotion.program.is.running");
								errMsg += "\n";
							}
							//Kiem tra ten chuong trinh
							String promotionName  = rowData.get(1).toUpperCase();
							if (promotionName.isEmpty()) {
								errMsg += R.getResource("catalog.promotion.import.column.null", "Tên CTKM");
							} else {
								if(promotionName.length() > 500){
									errMsg += R.getResource("catalog.promotion.import.over.max.length") + "\n";
									errMsg = errMsg.replaceAll("%max%", "500");
									errMsg = errMsg.replaceAll("%colName%", "Tên CTKM");
								}
								else
								{
								errMsg += ValidateUtil.validateField(promotionName, "catalog.promotion.name", 500, ConstantManager.ERR_EXIST_SPECIAL_CHAR_IN_NAME);	
								}
							}
							//Kiem tra loai Chuong trinh
							String typePromotion = rowData.get(3).toUpperCase();
							if(typePromotion.isEmpty())
							{
								errMsg += Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "common.required", Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "catalog.promotion.type"));
								errMsg += "\n";
							}
							else
							{
								if(!PROMOTION_TYPES.contains(typePromotion))
								{
									errMsg += Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "catalog.exists.before", Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "catalog.promotion.type"));
									errMsg += "\n";
								}
							}
							fromDate = null;
//							toDate = null;
							Date promotionBeginDate = null;
							//kiem tra TU NGAY
							String fromDatePromo = rowData.get(4);
							if(fromDatePromo.isEmpty())
							{
								errMsg += Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "common.required", Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "imp.epx.tuyen.clmn.tuNgay"));
								errMsg += "\n";
							}
							else
							{
								try {
									fromDate = dateFormat.parse(fromDatePromo);
									promotionBeginDate = fromDate;
									if(fromDate!=null&&fromDate.before(dateFormat.parse(dateFormat.format(new Date()))))
									{
										errMsg += R.getResource("common.invalid.format.date.before", R.getResource("imp.epx.tuyen.clmn.tuNgay"),R.getResource("imp.epx.tuyen.clmn.currentDate"));
										errMsg += "\n";
									}
								} catch (ParseException e) {
									// TODO Auto-generated catch block
									errMsg += R.getResource("common.invalid.format.date", R.getResource("imp.epx.tuyen.clmn.tuNgay"));
									errMsg += "\n";
								}
							}
							
							//kiem tra DEN NGAY
							String toDatePromo = rowData.get(5);
							if(!toDatePromo.isEmpty())
							{
								try {
									toDate = dateFormat.parse(toDatePromo);
									if(fromDate!=null && fromDate.after(toDate))
									{
										errMsg += R.getResource("common.fromdate.greater.todate") + "\n";
										errMsg += "\n";
									}
								} catch (ParseException e) {
									// TODO Auto-generated catch block
									errMsg += R.getResource("common.invalid.format.date", R.getResource("imp.epx.tuyen.clmn.denNgay"));
									errMsg += "\n";
								}
							}
							//kiem tra so thong bao
							String noticeCode = rowData.get(6).toUpperCase();
							if(!noticeCode.isEmpty()){
								if(noticeCode.length() > 100){
									errMsg += R.getResource("catalog.promotion.import.over.max.length") + "\n";
									errMsg = errMsg.replaceAll("%max%", "100");
									errMsg = errMsg.replaceAll("%colName%", "Số thông báo");
								}
								else
								{
								errMsg += ValidateUtil.validateField(noticeCode, "catalog.promotion.noticecode", 100, ConstantManager.ERR_EXIST_SPECIAL_CHAR_IN_SPECIAL);	
								}
							}
							else
							{
								errMsg += R.getResource("catalog.promotion.import.notice.code.obligate") + "\n";
							}
							 
							//kiem tra ten / nhom san pham
							String groupProductName = rowData.get(7);
							
							if (!groupProductName.isEmpty()) {
								if(groupProductName.length()>1000)
								{
									errMsg += R.getResource("catalog.promotion.import.over.max.length") + "\n";
									errMsg = errMsg.replaceAll("%max%", "1000");
									errMsg = errMsg.replaceAll("%colName%", "Nhóm/Tên SP hàng bán");
								}
								else
								{
									errMsg += ValidateUtil.validateField(groupProductName, "catalog.promotion.descriptionproduct", 1000, ConstantManager.ERR_EXIST_SPECIAL_CHAR_IN_SPECIAL);
								}
							}
							else
							{
								errMsg += R.getResource("catalog.promotion.import.description.product.obligate") + "\n";
							}
							
							// kiem tra mo ta chuong trinh
							String desPromotion = rowData.get(8);
							if(desPromotion.length()>1000)
							{
								errMsg += R.getResource("catalog.promotion.import.over.max.length") + "\n";
								errMsg = errMsg.replaceAll("%max%", "1000");
								errMsg = errMsg.replaceAll("%colName%", "Mô tả chương trình");
							}
							// kiem tra boi so
							String multiply = rowData.get(9);
							if(!multiply.isEmpty())
							if(!("0".equals(multiply) || "1".equals(multiply)))
							{
								errMsg += Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "catalog.promotion.import.multiple.incorrect.format") + "\n";
							}
							String 	rescursive = rowData.get(10);
							// kiem tra toi uu
							if(!rescursive.isEmpty())
							if(!("0".equals(rescursive) || "1".equals(rescursive)))
							{
								errMsg += Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "catalog.promotion.import.recursive.incorrect.format") + "\n";
							}
							//Kiem tra LOAI TRA THUONG
							String paymentType = rowData.get(11) ;
							if(!("2".equals(paymentType) || "1".equals(paymentType) || (FREE_ITEMS_REWARD_TYPES.contains(typePromotion)&&paymentType.isEmpty())))
							{
								errMsg += R.getResource("ctkm.import.new.product.rewardtype");
								errMsg += "\n";
							}
							else
							{
								if(FREE_ITEMS_REWARD_TYPES.contains(typePromotion)&&"2".equals(paymentType))
								{
									errMsg += R.getResource("catalog.promotion.import.voucher.not.use");
									errMsg += "\n";
								}
							}
							//
							fromDate = null;
//							toDate = null;
							//kiem tra TU NGAY TRA THUONG
							String fromDateReturn = rowData.get(12);
							if(!fromDateReturn.isEmpty())
							{
								if(!"2".equals(paymentType))
								{
									errMsg += R.getResource("catalog.promotion.import.reward.date.not.use");
									errMsg += "\n";
								}
								else
								try {
									fromDate = dateFormat.parse(fromDateReturn);
									if(fromDate!=null&&fromDate.before(dateFormat.parse(dateFormat.format(new Date()))))
									{
										errMsg += R.getResource("common.invalid.format.date.before", R.getResource("imp.epx.tuyen.clmn.tuNgay.traThuong"),R.getResource("imp.epx.tuyen.clmn.currentDate"));
										errMsg += "\n";
									}
								} catch (ParseException e) {
									// TODO Auto-generated catch block
									errMsg += R.getResource("common.invalid.format.date", R.getResource("imp.epx.tuyen.clmn.tuNgay.traThuong"));
									errMsg += "\n";
								}

								if(promotionBeginDate!=null&&fromDate!=null&&fromDate.before(promotionBeginDate))
								{
									errMsg += R.getResource("common.invalid.format.date.before", R.getResource("imp.epx.tuyen.clmn.tuNgay.traThuong"),R.getResource("imp.epx.tuyen.clmn.tuNgay"));
									errMsg += "\n";
								}
							}
							
							//kiem tra DEN NGAY TRA THUONG
							String toDateReturn = rowData.get(13);
							if(!toDateReturn.isEmpty())
							{
								try {
									toDate = dateFormat.parse(toDateReturn);
									if(fromDate!=null && fromDate.after(toDate))
									{
										errMsg += R.getResource("common.fromdate.greater.todate.reward") + "\n";
										errMsg += "\n";
									}
								} catch (ParseException e) {
									// TODO Auto-generated catch block
									errMsg += R.getResource("common.invalid.format.date", R.getResource("imp.epx.tuyen.clmn.denNgay.traThuong"));
									errMsg += "\n";
								}
							}
							
							if(!errMsg.isEmpty())
							{
								CellBean cb = new CellBean();
								cb.setContent1(data[0]);
								cb.setContent2(data[1]);
								cb.setContent3(data[2]);
								cb.setContent4(data[3]);
								cb.setContent5(data[4]);
								cb.setContent6(data[5]);
								cb.setContent7(data[6]);
								cb.setContent8(data[7]);
								cb.setContent9(data[8]);
								cb.setContent10(data[9]);
								cb.setContent11(data[10]);
								cb.setContent12(data[11]);	
								cb.setContent13(data[12]);
								cb.setContent14(data[13]);
								cb.setErrMsg(errMsg);
								infoPromotionError.add(cb);
							}
							
							if(!promotionProgramCodes.contains(promotionCode))
							{
								promotionProgramCodes.add(promotionCode);
							}
							String programAndTypeCode = promotionCode + typePromotion;
							if(!programAndTypeCodes.contains(programAndTypeCode))
							{
								programAndTypeCodes.add(programAndTypeCode);
							}
							infoPromotion.add(rowData);	
						}
					}
					if(promotionDetailSheet != null)
					{
						//doc sheet CO CAU
						Iterator<?> rowIter = promotionDetailSheet.rowIterator();
						rowIter.next();
						while (rowIter.hasNext()) {
							Row currentRow = (Row) rowIter.next();
							isContinue = true;
							for(int i=0;i<NUM_COL_PROMOTION_DETAIL_SHEET;i++)
							{
								if (currentRow.getCell(i) != null && !StringUtil.isNullOrEmpty(getCellValueToString(currentRow.getCell(i)))) {
									isContinue = false;
									break;
								}
							}
							if(isContinue) continue;
							String[] data = new String[NUM_COL_PROMOTION_DETAIL_SHEET];
							//MA CHUONG TRINH KHUYEN MAI
							data[0] = getCellValueToString(currentRow.getCell(0));
							//LOAI CHUONG TRINH KHUYEN MAI
							data[1] = getCellValueToString(currentRow.getCell(1));
							//MA NHOM
							data[2] = getCellValueToString(currentRow.getCell(2));
							//MA MUC
							data[3] = getCellValueToString(currentRow.getCell(3));
							//MUC CHA
							data[4] = getCellValueToString(currentRow.getCell(4));
							//MUC CON
							data[5] = getCellValueToString(currentRow.getCell(5));
							//MA SAN PHAM MUA
							data[6] = getCellValueToString(currentRow.getCell(6));
							//SO LUONG SAN PHAM MUA
							data[7] = getCellValueToString(currentRow.getCell(7));
							//DON VI TINH CHO SP MUA
							data[8] = getCellValueToString(currentRow.getCell(8));
							//SO TIEN SP MUA
							data[9] = getCellValueToString(currentRow.getCell(9));
							//THUOC TINH BAT BUOC CHO SP MUA
							data[10] = getCellValueToString(currentRow.getCell(10));
							//SO TIEN SP KM
							data[11] = getCellValueToString(currentRow.getCell(11));
							//% KM
							data[12] = getCellValueToString(currentRow.getCell(12));
							//MA SP KM
							data[13] = getCellValueToString(currentRow.getCell(13));
							//SO LUONG KM
							data[14] = getCellValueToString(currentRow.getCell(14));
							//DON VI TINH CHO SPKM
							data[15] = getCellValueToString(currentRow.getCell(15));
							//THUOC TINH BAT BUOC
							data[16] = getCellValueToString(currentRow.getCell(16));
							for(int i=0;i<NUM_COL_PROMOTION_DETAIL_SHEET;i++)
							{
								data[i] = data[i].trim();
							}
							List<String> rowData = Arrays.asList(data);
					//KIEM TRA SHEET CO CAU
							errMsg = "";
							// KIEM TRA MA CHUONG TRINH
							String promotionCode = rowData.get(0).toUpperCase();
							if(!promotionCode.isEmpty())
							{
								errMsg += ValidateUtil.validateField(promotionCode, "catalog.promotion.import.column.progcode", 
										                             50, ConstantManager.ERR_REQUIRE, 
																	 ConstantManager.ERR_MAX_LENGTH, 
										                             ConstantManager.ERR_EXIST_SPECIAL_CHAR_IN_CODE);
								PromotionProgram existPromotion = null;
								try {
									existPromotion = promotionProgramMgr.getPromotionProgramByCode(promotionCode);
								} catch (BusinessException e) {
									// TODO Auto-generated catch block
									LogUtility.logErrorStandard(e, R.getResource("web.log.message.error", "vnm.web.action.program.PromotionCatalogAction.getDataImportExcelPromotion()"), createLogErrorStandard(commonMgr.getSysDate()));
								}
								if (existPromotion != null && !ActiveType.WAITING.equals(existPromotion.getStatus())&&errMsg.isEmpty()) {
									errMsg += Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "catalog.promotion.program.is.running");
									errMsg += "\n";
								}
								if(!promotionProgramCodes.contains(promotionCode))
								{
									errMsg += R.getResource("catalog.promotion.import.not.init") + "\n";
								}
								
							}
							else
							{
								errMsg += Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "common.required", Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "catalog.promotion.code"));
								errMsg += "\n";
							}
							//KIEM TRA LOAI CHUONG TRINH
							String typePromotion = rowData.get(1).toUpperCase();
							if(typePromotion.isEmpty())
							{
								errMsg += Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "common.required", Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "catalog.promotion.type"));
								errMsg += "\n";
							}
							else
							{
								if(!PROMOTION_TYPES.contains(typePromotion))
								{
									errMsg += Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "catalog.exists.before", Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "catalog.promotion.type"));
									errMsg += "\n";
								}
								if(!programAndTypeCodes.contains(promotionCode+typePromotion))
								{
									errMsg += Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "catalog.promotion.import.type.is.not.same2") + "\n";
								}
							}
							//KIEM TRA MA NHOM
							String groupCode = rowData.get(2).toUpperCase();
							if(groupCode.isEmpty())
							{
								errMsg += Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "catalog.promotion.import.promotion.group.code.obligate");
								errMsg += "\n";
							}
							else
							{
								errMsg += ValidateUtil.validateField(groupCode, "catalog.promotion.import.column.groupcode", 
			                             50, ConstantManager.ERR_REQUIRE, 
										 ConstantManager.ERR_MAX_LENGTH, 
			                             ConstantManager.ERR_EXIST_SPECIAL_CHAR_IN_CODE);
							}
							//KIEM TRA MA MUC
							String levelCode = rowData.get(3).toUpperCase();
							if(levelCode.isEmpty())
							{
								errMsg += Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "catalog.promotion.import.promotion.level.code.obligate");
								errMsg += "\n";
							}
							else
							{
								errMsg += ValidateUtil.validateField(levelCode, "catalog.promotion.import.column.levelcode", 
			                             50, ConstantManager.ERR_REQUIRE, 
										 ConstantManager.ERR_MAX_LENGTH, 
			                             ConstantManager.ERR_EXIST_SPECIAL_CHAR_IN_CODE);
							}
							//KIEM TRA MUC CHA
							String parentLevel =  rowData.get(4).toUpperCase();
							if(!parentLevel.isEmpty())
							{
								if(!"X".equals(parentLevel))
								{
									errMsg+= R.getResource("catalog.promotion.import.selected.invalid",R.getResource("catalog.promotion.import.khtg.ctkm.clmn.parent"));
								}
							}
							
							//KIEM TRA MUC CON
							String childLevel =  rowData.get(5).toUpperCase();
							if(!childLevel.isEmpty())
							{
								if(!"X".equals(childLevel))
								{
									errMsg+= R.getResource("catalog.promotion.import.selected.invalid",R.getResource("catalog.promotion.import.khtg.ctkm.clmn.child"));
								}
							}
							if(!parentLevel.isEmpty()&&!childLevel.isEmpty())
							{
								errMsg+= Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "catalog.promotion.import.parent.and.child");
							}
							//KIEM TRA MA SAN PHAM
							String productCode = rowData.get(6).toUpperCase();
							if(!productCode.isEmpty())
							{
								Product product = productMgr.getProductByCode(productCode.trim());
								if (product == null) {
									errMsg += Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "common.not.exist.in.db", Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "catalog.buyproduct.code"));
									errMsg += "\n";
								}
								else
								{
									if(!ActiveType.RUNNING.equals(product.getStatus()))
									{
										errMsg += Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "ctkm.import.new.product.inactive", productCode);
										errMsg += "\n";
									}
								}
							}
							//KIEM TRA SO LUONG SP
							String productQuantity = rowData.get(7);
							if(!productQuantity.isEmpty())
							{

						        try {
						            Integer quantity = Integer.parseInt(productQuantity);
						            if(quantity<=0)
						            {
						            	errMsg += Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "catalog.promotion.import.column.invalid.format.number", "SL Sản Phẩm Mua");
						            }
						        } catch (NumberFormatException e) {
						        	errMsg += R.getResource("catalog.promotion.import.column.invalid.format.number", "SL Sản Phẩm Mua");
						        }

							}
							//KIEM TRA DON VI TINH CHO SP MUA
							String unitProduct = rowData.get(8).toUpperCase();
							if(!unitProduct.isEmpty())
							{
								if(!UNIT.contains(unitProduct))
								{
									errMsg+= Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "common.not.date", "Đơn Vị Tính cho SP Mua");
								}
							}
							else
							{
								if("X".equals(parentLevel))
								{
									errMsg+= Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "common.not.date", "Đơn Vị Tính cho SP Mua");
								}
							}
							//KIEM TRA SO TIEN SP MUA
							String amountProduct = rowData.get(9);
							if(!amountProduct.isEmpty())
							 try {
						            Double amount = Double.parseDouble(amountProduct);
						            if(amount<=0)
						            {
						            	errMsg += Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "catalog.promotion.import.column.invalid.format.number", "Số Tiền SP Mua");
						            }
						        } catch (NumberFormatException e) {
						        	errMsg += R.getResource("catalog.promotion.import.column.invalid.format.number", "Số Tiền SP Mua");
						      }
							//Kiem tra THUOC TINH BAT BUOC CHO SP MUA
							String productCondition =  rowData.get(10).toUpperCase();
							if(!productCondition.isEmpty())
							{
								if(!"X".equals(productCondition))
								{
									errMsg+= R.getResource("catalog.promotion.import.selected.invalid",R.getResource("catalog.promotion.import.khtg.ctkm.clmn.buy.required"));
								}
							}
							//KIEM TRA SO TIEN SP KM
							String discountAmount = rowData.get(11);
							if(!discountAmount.isEmpty())
							 try {
						            Double discount = Double.parseDouble(discountAmount);
						            if(discount<=0)
						            {
						            	errMsg += Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "catalog.promotion.import.column.invalid.format.number", "Số Tiền SP KM");
						            }
						        } catch (NumberFormatException e) {
						        	errMsg += R.getResource("catalog.promotion.import.column.invalid.format.number", "Số Tiền SP KM");
						      }
							//KIEM TRA % khuyen mai
							String percentPromo = rowData.get(12);
							if(!percentPromo.isEmpty())
							 try {
						            Double percent = Double.parseDouble(percentPromo);
						            if(percent<= (double) 0 || percent > (double) 100)
						            {
						            	errMsg += Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "ctkm.import.new.percent.zero");
						            }
						        } catch (NumberFormatException e) {
						        	errMsg += R.getResource("catalog.promotion.import.column.invalid.format.number", "% KM");
						      }
							//KIEM TRA MA SP Khuyen MAI
							String promoProductCode = rowData.get(13).toUpperCase();
							if(!promoProductCode.isEmpty())
							{
								Product promoProduct = productMgr.getProductByCode(promoProductCode);
								if (promoProduct == null) {
									errMsg += Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "common.not.exist.in.db", Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "catalog.disproduct.code"));
									errMsg += "\n";
								}
								else
								{
									if(!ActiveType.RUNNING.equals(promoProduct.getStatus()))
									{
										errMsg += Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "ctkm.import.new.product.inactive", promoProductCode);
										errMsg += "\n";
									}
								}
							}
							//KIEM TRA SO LUONG KM
							String promoQuantity = rowData.get(14);
							if(!promoQuantity.isEmpty())
							 try {
						            Integer quantity = Integer.parseInt(promoQuantity);
						            if(quantity<=0)
						            {
						            	errMsg += Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "catalog.promotion.import.column.invalid.format.number", "Số Lượng KM");
						            }
						        } catch (NumberFormatException e) {
						        	errMsg += R.getResource("catalog.promotion.import.column.invalid.format.number", "Số Lượng KM");
						      }
							//Kiem tra DON VI TINH CHO SP KHUYEN MAI
							String unitPromoProduct = rowData.get(15).toUpperCase();
							if(!unitPromoProduct.isEmpty())
							{
								if(!UNIT.contains(unitPromoProduct))
								{
									errMsg+= Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "common.not.date", "Đơn Vị Tính cho SP KM");
								}
							}
							//Kiem tra THUOC TINH BAT BUOC
							String promoCondition =  rowData.get(16).toUpperCase();
							if(!promoCondition.isEmpty())
							{
								if(!"X".equals(promoCondition))
								{
									errMsg+= R.getResource("catalog.promotion.import.selected.invalid",R.getResource("catalog.promotion.import.khtg.ctkm.clmn.KM.required"));
								}
							}
							//Kiem tra du lieu muc
							if(childLevel.isEmpty()&&parentLevel.isEmpty()&&promoProductCode.isEmpty()&&productCode.isEmpty())
							{
								errMsg+= R.getResource("catalog.promotion.import.required.product");
							}
							if(!errMsg.isEmpty())
							{
								CellBean cb = new CellBean();
								cb.setContent1(data[0]);
								cb.setContent2(data[1]);
								cb.setContent3(data[2]);
								cb.setContent4(data[3]);
								cb.setContent5(data[4]);
								cb.setContent6(data[5]);
								cb.setContent7(data[6]);
								cb.setContent8(data[7]);
								cb.setContent9(data[8]);
								cb.setContent10(data[9]);
								cb.setContent11(data[10]);
								cb.setContent12(data[11]);	
								cb.setContent13(data[12]);
								cb.setContent14(data[13]);
								cb.setContent15(data[14]);
								cb.setContent16(data[15]);
								cb.setContent17(data[16]);
								cb.setErrMsg(errMsg);
								infoPromotionDetailError.add(cb);
							}
							infoPromotionDetail.add(rowData);
						}
					}
					
					if(promotionShopSheet != null)
					{
						//doc sheet DON VI THAM GIA
						Iterator<?> rowIter = promotionShopSheet.rowIterator();
						rowIter.next();
						while (rowIter.hasNext()) {
							Row currentRow = (Row) rowIter.next();
							isContinue = true;
							for(int i=0;i<NUM_COL_PROMOTION_SHOP_SHEET;i++)
							{
								if (currentRow.getCell(i) != null && !StringUtil.isNullOrEmpty(getCellValueToString(currentRow.getCell(i)))) {
									isContinue = false;
									break;
								}
							}
							if(isContinue) continue;
							String[] data = new String[NUM_COL_PROMOTION_SHOP_SHEET];
							//MA CHUONG TRINH
							data[0] = getCellValueToString(currentRow.getCell(0));
							//MA DON VI
							data[1] = getCellValueToString(currentRow.getCell(1));
							//SO SUAT
							data[2] = getCellValueToString(currentRow.getCell(2));
							//SO TIEN
							data[3] = getCellValueToString(currentRow.getCell(3));
							//SO LUONG
							data[4] = getCellValueToString(currentRow.getCell(4));
							for(int i=0;i<NUM_COL_PROMOTION_SHOP_SHEET;i++)
							{
								data[i] = data[i].trim();
							}
							List<String> rowData = Arrays.asList(data);
							//Kiem tra
							errMsg = "";
							//kiem tra ma chuong trinh
							String promoCode = rowData.get(0);
							if (promoCode.isEmpty()) {
								errMsg += R.getResource("catalog.promotion.import.promotion.code.obligate") + "\n";
							} else {
								if(!promotionProgramCodes.contains(promoCode.toUpperCase()))
								{
									errMsg += R.getResource("catalog.promotion.import.not.init") + "\n";
								}
							}
							//Kiem tra MA DON VI
							String shopCode = rowData.get(1).toUpperCase();
							if(shopCode.isEmpty())
							{
								errMsg += R.getResource("catalog.promotion.import.unit.code.obligate") + "\n";
							}
							else
							{
								//Kiem tra don vi bi trung lap
//								boolean isDuplicated = false;
//								List<String> duplicatedShop = null;
//								for(int i = 0;i<infoPromotionShop.size();i++)
//								{
//									if(shopCode.equalsIgnoreCase(infoPromotionShop.get(i).get(1)))
//									{
//										isDuplicated = true;
//										duplicatedShop = infoPromotionShop.get(i);
//										break;
//									}
//								}
//
//								if (isDuplicated) {
//									errMsg += Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "promotion.product.import.duplicate", duplicatedShop);
//									errMsg += "\n";
//								}
								// Kiem tra don vi ton tai trong he thong
								if(shopMgr.getShopByCode(shopCode) == null){
									errMsg += R.getResource("catalog.promotion.import.unit.code.not.permission") + "\n";
								} else if (currentUser != null && currentUser.getShopRoot() != null){ // kiem tra don vi co thuoc quyen quan ly cua user
									List<Shop> listShopChild = promotionProgramMgr.getListChildByShopId(currentUser.getShopRoot().getShopId());
									// Kiem tra shop co thuoc quen quan ly cua user dang nhap
									boolean isShopMapWithUser = false;
									for(Shop shop: listShopChild){
										 if(shopCode.toLowerCase().equals(shop.getShopCode().toLowerCase())){
											 isShopMapWithUser = true;
											 break;
										}
									}
									if(!isShopMapWithUser){
										errMsg += R.getResource("catalog.promotion.import.unit.code.not.permission.by.current.user") + "\n";
									}
							}
							// kiem tra SO SUAT
								String quantityMax = rowData.get(2);
								if(!quantityMax.isEmpty()){
								if(quantityMax.length() > 9 ){
									errMsg += R.getResource("catalog.promotion.import.over.max.length") + "\n";
									errMsg = errMsg.replaceAll("%max%", "9");
									errMsg = errMsg.replaceAll("%colName%", "Số suất");
								}
								try  
								  { 
									Integer num = Integer.parseInt(quantityMax);
									if(num<0)
									{
										errMsg += Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "catalog.promotion.import.quantity.max.incorrect.format");
									}
								  }
								catch(NumberFormatException e)  
								  {
									errMsg += R.getResource("catalog.promotion.import.quantity.max.incorrect.format") + "\n";
								  }
								}
								// kiem tra SO TIEN
								String amountMax = rowData.get(3);
								if(!amountMax.isEmpty()){
								if(amountMax.length() > 9 ){
									errMsg += R.getResource("catalog.promotion.import.over.max.length") + "\n";
									errMsg = errMsg.replaceAll("%max%", "9");
									errMsg = errMsg.replaceAll("%colName%", "Số tiền");
								}
								try  
								  { 
									Double num = Double.parseDouble(amountMax);
									if(num<0)
									{
										errMsg += Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "catalog.promotion.import.amount.max.incorrect.format");
									}
								  }
								catch(NumberFormatException e)  
								  {
									errMsg += R.getResource("catalog.promotion.import.amount.max.incorrect.format") + "\n";
								  }
								}
								//kiemn tra SO LUONG
								String numMax = rowData.get(4);
								if (!numMax.isEmpty()) {
									if(numMax != null && numMax.length() > 9){
										errMsg += R.getResource("catalog.promotion.import.over.max.length") + "\n";
										errMsg = errMsg.replaceAll("%max%", "9");
										errMsg = errMsg.replaceAll("%colName%", "Số lượng");
									}
									try  
									  { 
										Integer num = Integer.parseInt(amountMax);
										if(num<0)
										{
											errMsg += Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "catalog.promotion.import.num.max.incorrect.format");
										}
									  }
									catch(NumberFormatException e)  
									  {
										errMsg += R.getResource("catalog.promotion.import.num.max.incorrect.format") + "\n";
									  }

								}
							}
							if(!errMsg.isEmpty())
							{
								CellBean cb = new CellBean();
								cb.setContent1(data[0]);
								cb.setContent2(data[1]);
								cb.setContent3(data[2]);
								cb.setContent4(data[3]);
								cb.setContent5(data[4]);
								cb.setErrMsg(errMsg);
								infoPromotionShopError.add(cb);
							}
							infoPromotionShop.add(rowData);
					}
				}
			} 
			}
			catch (FileNotFoundException e) {
				LogUtility.logError(e, "PromotionCatalogAction.getDataImportExcelPromotion()" + e.getMessage());
			} catch (InvalidFormatException e) {
				// TODO Auto-generated catch block
				LogUtility.logError(e, "PromotionCatalogAction.getDataImportExcelPromotion()" + e.getMessage());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				LogUtility.logError(e, "PromotionCatalogAction.getDataImportExcelPromotion()" + e.getMessage());
			} catch (BusinessException e) {
				// TODO Auto-generated catch block
				LogUtility.logError(e, "PromotionCatalogAction.getDataImportExcelPromotion()" + e.getMessage());
			} catch (DataAccessException e) {
				// TODO Auto-generated catch block
				LogUtility.logError(e, "PromotionCatalogAction.getDataImportExcelPromotion()" + e.getMessage());
			}	
			
			
	}
		
	private List<PromotionImportNewVO> convertDataImportExcelPromotion (List<List<String>> infoPromotions, List<List<String>> infoPromotionDetails, List<List<String>> infoPromotionShops, List<CellBean> infoPromotionError, List<CellBean> infoPromotionDetailError, List<CellBean> infoPromotionShopError) {
		List<PromotionImportNewVO> promotionImportNewVOs = new ArrayList<>();
		boolean isExist = false;
		final List<String> UNIT = Arrays.asList(R.getResource("ctkm.import.new.le"), R.getResource("ctkm.import.new.thung"));
		final List<String> ALLOWED_RECURSIVE_TYPES = Arrays.asList("ZV02","ZV03","ZV05","ZV06","ZV08","ZV09","ZV11","ZV12","ZV13","ZV14","ZV15","ZV16","ZV17","ZV18","ZV20","ZV21","ZV23","ZV24");
		final List<String> ALLOWED_MUTIPLE_TYPES = Arrays.asList("ZV02","ZV03","ZV05","ZV06","ZV08","ZV09","ZV11","ZV12","ZV14","ZV15","ZV17","ZV18","ZV20","ZV21","ZV23","ZV24");
		for(List<String> infoPromotion : infoPromotions) {
			// xu ly lấy thông tin CTKM
			PromotionImportNewVO promotionImportNewVO = new PromotionImportNewVO();
			//Danh sach don vi tham gia
			List<PromotionImportShopNewVO> listPromotionImportShopNew = new ArrayList<>();
						
			if(promotionImportNewVOs.size()>0) {
				for(PromotionImportNewVO promotionImportNewVOcheck: promotionImportNewVOs){
					if(infoPromotion.get(0).equals(promotionImportNewVOcheck.getPromotionCode())){
						promotionImportNewVO = promotionImportNewVOcheck;
						isExist = true;
						break;
					}
				}
			}
			
			// ghi de lai cac attribute cu
			if(!isExist) {
				promotionImportNewVOs.add(promotionImportNewVO);
			}
			isExist = false;
			promotionImportNewVO.setPromotionCode(!StringUtil.isNullOrEmpty(infoPromotion.get(0)) ? infoPromotion.get(0).toUpperCase() : null);
			promotionImportNewVO.setPromotionName(!StringUtil.isNullOrEmpty(infoPromotion.get(1)) ? infoPromotion.get(1) : null);
			promotionImportNewVO.setVersion(!StringUtil.isNullOrEmpty(infoPromotion.get(2)) ? infoPromotion.get(2) : null);
			promotionImportNewVO.setType(!StringUtil.isNullOrEmpty(infoPromotion.get(3)) ? infoPromotion.get(3).toUpperCase() : null);
			promotionImportNewVO.setFromDate(DateUtil.parse(!StringUtil.isNullOrEmpty(infoPromotion.get(4)) ? infoPromotion.get(4) : null, DateUtil.DATE_FORMAT_STR));
			promotionImportNewVO.setToDate(DateUtil.parse(!StringUtil.isNullOrEmpty(infoPromotion.get(5)) ? infoPromotion.get(5) : null, DateUtil.DATE_FORMAT_STR));
			promotionImportNewVO.setNotice(!StringUtil.isNullOrEmpty(infoPromotion.get(6)) ? infoPromotion.get(6).toUpperCase() : null);
			promotionImportNewVO.setDescriptionProduct(!StringUtil.isNullOrEmpty(infoPromotion.get(7)) ? infoPromotion.get(7) : null);
			promotionImportNewVO.setDescription(!StringUtil.isNullOrEmpty(infoPromotion.get(8)) ? infoPromotion.get(8) : null);
			
			if (!StringUtil.isNullOrEmpty(infoPromotion.get(9))&&"1".equals(infoPromotion.get(9))&&ALLOWED_MUTIPLE_TYPES.contains(promotionImportNewVO.getType())) {
				promotionImportNewVO.setMultiple(true);
			}else{
				promotionImportNewVO.setMultiple(false);
			}
			if (!StringUtil.isNullOrEmpty(infoPromotion.get(10))&&"1".equals(infoPromotion.get(9))&&ALLOWED_RECURSIVE_TYPES.contains(promotionImportNewVO.getType())) {
				promotionImportNewVO.setRecursive(true);
			}else{
				promotionImportNewVO.setRecursive(false);
			}
			
			promotionImportNewVO.setRewardType(!StringUtil.isNullOrEmpty(infoPromotion.get(11)) ? Integer.parseInt(infoPromotion.get(11)) : null);
			promotionImportNewVO.setApplyFromDate(!StringUtil.isNullOrEmpty(infoPromotion.get(12)) ? DateUtil.parse(infoPromotion.get(12), DateUtil.DATE_FORMAT_STR) : null);
			promotionImportNewVO.setApplyToDate(!StringUtil.isNullOrEmpty(infoPromotion.get(13)) ? DateUtil.parse(infoPromotion.get(13), DateUtil.DATE_FORMAT_STR) : null);
		
			// xu ly cho danh sach đon vi tham gia
			for(List<String> infoPromotionShop : infoPromotionShops) {
				if(infoPromotionShop.get(0).equalsIgnoreCase(promotionImportNewVO.getPromotionCode())) {
					PromotionImportShopNewVO promotionImportShopNewVO = new PromotionImportShopNewVO();
					int index = checkDupShopForPromotionNew(listPromotionImportShopNew, infoPromotionShop.get(1));
					if (index != -1) {
						promotionImportShopNewVO = listPromotionImportShopNew.get(index);
					}
					
					promotionImportShopNewVO.setShopCode(!StringUtil.isNullOrEmpty(infoPromotionShop.get(1)) ? infoPromotionShop.get(1) : null);
					promotionImportShopNewVO.setQuantity(!StringUtil.isNullOrEmpty(infoPromotionShop.get(2)) ? Integer.parseInt(infoPromotionShop.get(2)) : null);
					promotionImportShopNewVO.setAmount(!StringUtil.isNullOrEmpty(infoPromotionShop.get(3)) ? new BigDecimal(infoPromotionShop.get(3)) : null);
					promotionImportShopNewVO.setNum(!StringUtil.isNullOrEmpty(infoPromotionShop.get(4)) ? new BigDecimal(infoPromotionShop.get(4)) : null);			
					listPromotionImportShopNew.add(promotionImportShopNewVO);
				}
			}
			promotionImportNewVO.setShops(listPromotionImportShopNew);
			
			// xu ly cho danh sach cơ cấu 
			for(List<String> infoPromotionDetail : infoPromotionDetails) {
				if(infoPromotionDetail.get(0).equalsIgnoreCase(promotionImportNewVO.getPromotionCode())) {
					if(promotionImportNewVO.getProductGroup() == null) {
						promotionImportNewVO.setProductGroup(new ArrayList<PromotionImportProductGroupNewVO>());
					}
					
					//tao nhóm cho CTKM
					PromotionImportProductGroupNewVO groupNewVO = new PromotionImportProductGroupNewVO();
					for(int i = 0; i < promotionImportNewVO.getProductGroup().size(); i++) {
						if(promotionImportNewVO.getProductGroup().get(i).getGroupCode().equals(infoPromotionDetail.get(2))) {
							groupNewVO = promotionImportNewVO.getProductGroup().get(i);
							isExist = true;
							break;
						}
					}
					if(!isExist) {
						promotionImportNewVO.getProductGroup().add(groupNewVO);
					}
					isExist = false;
					//set thong tin nhoms
					groupNewVO.setGroupCode(infoPromotionDetail.get(2));
					groupNewVO.setGroupName(groupNewVO.getGroupCode());
					groupNewVO.setMultiple(promotionImportNewVO.isMultiple());
					groupNewVO.setRecursive(promotionImportNewVO.isRecursive());
					if("X".equals(infoPromotionDetail.get(4).toUpperCase())) {
						groupNewVO.setUnit(getUnitForPromotionNew(UNIT, infoPromotionDetail.get(8)));
					}
					
					// tao muc cho CTKM(Mua - KM)
					List<PromotionImportGroupLevelNewVO> groupLevelBuys = new ArrayList<>();
					List<PromotionImportGroupLevelNewVO> groupLevelKMs = new ArrayList<>();					
					if(groupNewVO.getGroupLevelBuys() == null) {
						groupNewVO.setGroupLevelBuys(groupLevelBuys);
						groupNewVO.setGroupLevelKMs(groupLevelKMs);
					} else {
						groupLevelBuys = groupNewVO.getGroupLevelBuys();
						groupLevelKMs = groupNewVO.getGroupLevelKMs();
					}
					
					PromotionImportGroupLevelNewVO groupLevelNewVOMua = new PromotionImportGroupLevelNewVO();
					PromotionImportGroupLevelNewVO groupLevelNewVOKM = new PromotionImportGroupLevelNewVO();
					for(int i = 0; i < groupNewVO.getGroupLevelBuys().size(); i++) {
						if(groupNewVO.getGroupLevelBuys().get(i).getGroupLevelCode().equals(infoPromotionDetail.get(3))) {
							groupLevelNewVOMua = groupNewVO.getGroupLevelBuys().get(i);
							groupLevelNewVOKM = groupNewVO.getGroupLevelKMs().get(i);
							isExist = true;
							break;
						}
					}
					
					if(!isExist) {
						groupLevelBuys.add(groupLevelNewVOMua);
						groupLevelKMs.add(groupLevelNewVOKM);
					}
					isExist = false;
					
					// set thong tin chung cho muc
					groupLevelNewVOMua.setGroupLevelCode(infoPromotionDetail.get(3));
					groupLevelNewVOKM.setGroupLevelCode(infoPromotionDetail.get(3));
					if("X".equals(infoPromotionDetail.get(4).toUpperCase())) {
						groupLevelNewVOMua.hasParent(true);
						groupLevelNewVOMua.setQuantity(StringUtil.isNullOrEmpty(infoPromotionDetail.get(7))? null:Integer.parseInt(infoPromotionDetail.get(7)));
						groupLevelNewVOMua.setAmount(StringUtil.isNullOrEmpty(infoPromotionDetail.get(9))? null:new BigDecimal(infoPromotionDetail.get(9)));
						groupLevelNewVOMua.setQuantityUnit(groupNewVO.getUnit());
						
						groupLevelNewVOKM.hasParent(true);
						groupLevelNewVOKM.setQuantity(StringUtil.isNullOrEmpty(infoPromotionDetail.get(14))? null:Integer.parseInt(infoPromotionDetail.get(14)));
						groupLevelNewVOKM.setAmount(StringUtil.isNullOrEmpty(infoPromotionDetail.get(11))? null:new BigDecimal(infoPromotionDetail.get(11)));
						groupLevelNewVOKM.setPercent(StringUtil.isNullOrEmpty(infoPromotionDetail.get(12))? null:Float.parseFloat((infoPromotionDetail.get(12))));
						groupLevelNewVOKM.setQuantityUnit(1);
					}
					
					//detail cho muc mua
					List<PromotionImportGroupLevelDetailNewVO> detailNewVOMuas = new ArrayList<>();
					if(groupLevelNewVOMua.getGroupLevelDetails() != null) {
						detailNewVOMuas = groupLevelNewVOMua.getGroupLevelDetails();
					} else {
						groupLevelNewVOMua.setGroupLevelDetails(detailNewVOMuas);
					}
					if(!"X".equals(infoPromotionDetail.get(4).toUpperCase()) && !StringUtil.isNullOrEmpty(infoPromotionDetail.get(6))) {
						// tao detail muc mua
						PromotionImportGroupLevelDetailNewVO detailNewVOMua = new PromotionImportGroupLevelDetailNewVO();
						for(int i = 0; i < detailNewVOMuas.size(); i++) {
							if(detailNewVOMuas.get(i).getProductCode().equals(infoPromotionDetail.get(6))) {
								if((detailNewVOMuas.get(i).isChild() && "X".equals(infoPromotionDetail.get(5).toUpperCase())) || (!detailNewVOMuas.get(i).isChild() && !"X".equals(infoPromotionDetail.get(5).toUpperCase()))) {
									detailNewVOMua = detailNewVOMuas.get(i);
									isExist = true;
									break;
								}
							}
						}						
						if(!isExist) {
							detailNewVOMuas.add(detailNewVOMua);
						}						
						isExist = false;
						//set du lieu cho detail muc mua
						detailNewVOMua.setProductCode(infoPromotionDetail.get(6));
						detailNewVOMua.setRequired("X".equalsIgnoreCase(infoPromotionDetail.get(10))? true:false);
						detailNewVOMua.setChild("X".equalsIgnoreCase(infoPromotionDetail.get(5))? true:false);
						detailNewVOMua.setQuantity(StringUtil.isNullOrEmpty(infoPromotionDetail.get(7))? null:Integer.parseInt(infoPromotionDetail.get(7)));
						detailNewVOMua.setAmount(StringUtil.isNullOrEmpty(infoPromotionDetail.get(9))? null:new BigDecimal(infoPromotionDetail.get(9)));
					}	
					
					//detail cho muc KM
					List<PromotionImportGroupLevelDetailNewVO> detailNewVOKMs = new ArrayList<>();
					if(groupLevelNewVOKM.getGroupLevelDetails() != null) {
						detailNewVOKMs = groupLevelNewVOKM.getGroupLevelDetails();
					} else {
						groupLevelNewVOKM.setGroupLevelDetails(detailNewVOKMs);
					}
					if(!"X".equals(infoPromotionDetail.get(4).toUpperCase()) && !StringUtil.isNullOrEmpty(infoPromotionDetail.get(13))) {
						// tao detail muc KM
						PromotionImportGroupLevelDetailNewVO detailNewVOKM = new PromotionImportGroupLevelDetailNewVO();
						for(int i = 0; i < detailNewVOKMs.size(); i++) {
							if(detailNewVOKMs.get(i).getProductCode().equals(infoPromotionDetail.get(13))) {
								detailNewVOKM = detailNewVOKMs.get(i);
								isExist = true;
								break;
							}
						}						
						if(!isExist) {
							detailNewVOKMs.add(detailNewVOKM);
						}						
						isExist = false;
						//set du lieu cho detail muc KM
						detailNewVOKM.setProductCode(infoPromotionDetail.get(13));
						detailNewVOKM.setRequired("X".equalsIgnoreCase(infoPromotionDetail.get(16))? true:false);
						detailNewVOKM.setQuantity(StringUtil.isNullOrEmpty(infoPromotionDetail.get(14))? null:Integer.parseInt(infoPromotionDetail.get(14)));												
					}
				}
			}
		}
		return promotionImportNewVOs;
	}
		
	private int checkDupShopForPromotionNew(List<PromotionImportShopNewVO> shops, String shopCode) {
		if(shops != null && shops.size() > 0) {
			for(int i = 0; i < shops.size(); i++) {
				if(shops.get(i).getShopCode().trim().equals(shopCode.trim())) {
					return i;
				}
			}
		}
		return -1;
	}
	
	private int checkDupShopForPromotion(List<PromotionImportShopVO> shops, String shopCode) {
		if(shops != null && shops.size() > 0) {
			for(int i = 0; i < shops.size(); i++) {
				if(shops.get(i).getShopCode().trim().equals(shopCode.trim())) {
					return i;
				}
			}
		}
		return -1;
	}
	
	private int getUnitForPromotionNew(List<String> units, String value) {
		if(StringUtil.isNullOrEmpty(value)) {
			return 1;
		}
		int unit = units.indexOf(value.toUpperCase()) + 1;
		if(unit == 0) {
			unit = 1;
		}
		
		return unit;
	}
		
	private List<PromotionImportNewVO> validatePromotionImport (List<PromotionImportNewVO> importNewVOs, List<PromotionImportNewVO> promotionImportNewErrorVOs) {
		List<PromotionImportNewVO> promotionImportNewVOs = new ArrayList<>();
		for(PromotionImportNewVO importNewVO : importNewVOs) {
			String message = "";
			if(PromotionType.ZV07.getValue().equals(importNewVO.getType())) {
				message = validateZV07New(importNewVO);
			} else if(PromotionType.ZV08.getValue().equals(importNewVO.getType())) {
				message = validateZV08New(importNewVO);
			} else if(PromotionType.ZV09.getValue().equals(importNewVO.getType())) {
				message = validateZV09New(importNewVO);
			} else if(PromotionType.ZV10.getValue().equals(importNewVO.getType())) {
				message = validateZV10New(importNewVO);
			} else if(PromotionType.ZV11.getValue().equals(importNewVO.getType())) {
				message = validateZV11New(importNewVO);
			} else if(PromotionType.ZV12.getValue().equals(importNewVO.getType())) {
				message = validateZV12New(importNewVO);
			} else if(PromotionType.ZV19.getValue().equals(importNewVO.getType())) {
				message = validateZV19New(importNewVO);
			} else if(PromotionType.ZV20.getValue().equals(importNewVO.getType())) {
				message = validateZV20New(importNewVO);
			} else if(PromotionType.ZV21.getValue().equals(importNewVO.getType())) {
				message = validateZV21New(importNewVO);
			} else {
				// lỗi loại CTKM không hợp lệ
				importNewVO.setMessageError(R.getResource("ctkm.import.new.program.type.not.process", importNewVO.getType()));
				promotionImportNewErrorVOs.add(importNewVO);
				continue;
			}
			
			if(!StringUtil.isNullOrEmpty(message)) {
				// lỗi CTKM
				promotionImportNewErrorVOs.add(importNewVO);
				continue;
			}
			
			promotionImportNewVOs.add(importNewVO);
		}
		return promotionImportNewVOs;
	}
		
	private List<PromotionImportNewVO> sortPromotionImport (List<PromotionImportNewVO> importNewVOs) {	
		// danh index cho muc theo index của list groupLevel và sort mức
		for(PromotionImportNewVO importNewVO : importNewVOs) {
			for(PromotionImportProductGroupNewVO groupNewVO : importNewVO.getProductGroup()) {
				List<PromotionImportGroupLevelNewVO> groupLevelBuys = groupNewVO.getGroupLevelBuys();
				for(int i = 0; i < groupLevelBuys.size(); i++) {
					groupLevelBuys.get(i).setIndex(i + 1);
				}				
			}
		}
		
		// sort mức mua
		List<String> lstZVQuantity = Arrays.asList(PromotionType.ZV07.getValue(), PromotionType.ZV08.getValue(), PromotionType.ZV09.getValue());
		List<String> lstZVsmount = Arrays.asList(PromotionType.ZV10.getValue(), PromotionType.ZV11.getValue(), PromotionType.ZV12.getValue(), PromotionType.ZV19.getValue(), PromotionType.ZV20.getValue(), PromotionType.ZV21.getValue());
		for(PromotionImportNewVO importNewVO : importNewVOs) {
			for(PromotionImportProductGroupNewVO groupNewVO : importNewVO.getProductGroup()) {
				List<PromotionImportGroupLevelNewVO> groupLevelBuys = groupNewVO.getGroupLevelBuys();
				if(lstZVQuantity.contains(importNewVO.getType())) {
					Collections.sort(groupLevelBuys, new Comparator<PromotionImportGroupLevelNewVO>() {
						@Override
						public int compare(PromotionImportGroupLevelNewVO arr1, PromotionImportGroupLevelNewVO arr2) {
							if(arr1.getQuantity() > arr2.getQuantity()) {
								return -1;
							}
							
							if(arr1.getQuantity() < arr2.getQuantity()) {
								return 1;
							}
							return 0;
						}							
					});	
				} else if(lstZVsmount.contains(importNewVO.getType())) {
					Collections.sort(groupLevelBuys, new Comparator<PromotionImportGroupLevelNewVO>() {
						@Override
						public int compare(PromotionImportGroupLevelNewVO arr1, PromotionImportGroupLevelNewVO arr2) {
							if(arr1.getAmount().compareTo(arr2.getAmount()) == 1) {
								return -1;
							}
							
							if(arr1.getAmount().compareTo(arr2.getAmount()) == -1) {
								return 1;
							}
							return 0;
						}							
					});	
				}							
			}
		}		
		
		// sort mức khuyen mãi theo mức mua
		for(PromotionImportNewVO importNewVO : importNewVOs) {
			for(PromotionImportProductGroupNewVO groupNewVO : importNewVO.getProductGroup()) {
				List<PromotionImportGroupLevelNewVO> groupLevelBuys = groupNewVO.getGroupLevelBuys();
				List<PromotionImportGroupLevelNewVO> tmpGroupLevelKMs = groupNewVO.getGroupLevelKMs();
				List<PromotionImportGroupLevelNewVO> groupLevelKMs = new ArrayList<>();
				for(int i = 0; i < groupLevelBuys.size(); i++) {
					groupLevelKMs.add(tmpGroupLevelKMs.get(groupLevelBuys.get(i).getIndex() - 1));
				}
				groupNewVO.setGroupLevelKMs(groupLevelKMs);
			}
		}
		return importNewVOs;
	}
		
	private void createSubConditionForKM (List<PromotionImportGroupLevelDetailNewVO> detailNewVOs, int promotionType, Map<Integer, List<String>> subGroupLevelDetailQuantity, Map<BigDecimal, List<String>> subGroupLevelDetailAmount, List<String> productCodes, List<String> productCodesForLevel) {
		for(int j = 0; j < detailNewVOs.size(); j++) {
			PromotionImportGroupLevelDetailNewVO detailNewVOEx1 = detailNewVOs.get(j);
			if(detailNewVOEx1.isChild()) {
				if(productCodes.size() == 0 || productCodes.indexOf(detailNewVOEx1.getProductCode()) == -1){
					productCodes.add(detailNewVOEx1.getProductCode());
				}
				Integer quantity = detailNewVOEx1.getQuantity() != null? detailNewVOEx1.getQuantity():0;
				BigDecimal amount = detailNewVOEx1.getAmount() != null? detailNewVOEx1.getAmount():BigDecimal.ZERO;
				
				if(promotionType == 1) {
					if(!subGroupLevelDetailAmount.containsKey(amount)) {
						subGroupLevelDetailAmount.put(amount, new ArrayList<String>());
					}
					if(subGroupLevelDetailAmount.get(amount).indexOf(detailNewVOEx1.getProductCode()) == -1) {
						subGroupLevelDetailAmount.get(amount).add(detailNewVOEx1.getProductCode());
					}
					
				} else {
					if(!subGroupLevelDetailQuantity.containsKey(quantity)) {
						subGroupLevelDetailQuantity.put(quantity, new ArrayList<String>());
					}
					if(subGroupLevelDetailQuantity.get(quantity).indexOf(detailNewVOEx1.getProductCode()) == -1) {
						subGroupLevelDetailQuantity.get(quantity).add(detailNewVOEx1.getProductCode());
					}
				}
			} else {
				if(productCodesForLevel.indexOf(detailNewVOEx1.getProductCode()) == -1) {
					productCodesForLevel.add(detailNewVOEx1.getProductCode());
				}
			}
		}
	}
	
	private String validateVoucherNew (PromotionImportNewVO importNewVO) {
		if(((Integer) 2).equals(importNewVO.getRewardType()) && Arrays.asList(ConstantManager.getSaleOrderBillPromotionVoucherTypeCode()).contains(importNewVO.getType())) {
			//voucher -> check fromDateApply, toDateApply
			if(importNewVO.getApplyFromDate() == null) {
				importNewVO.setMessageError(R.getResource("catalog.promotion.import.column.null", R.getResource("imp.epx.tuyen.clmn.tuNgay.traThuong")));
				return R.getResource("catalog.promotion.import.column.null", R.getResource("imp.epx.tuyen.clmn.tuNgay.traThuong"));
			}
			if(DateUtil.compareDateWithoutTime(importNewVO.getApplyFromDate(), DateUtil.now()) < 0) {
				importNewVO.setMessageError(R.getResource("common.date.greater.currentdate", R.getResource("imp.epx.tuyen.clmn.tuNgay.traThuong")));
				return R.getResource("common.date.greater.currentdate", R.getResource("imp.epx.tuyen.clmn.tuNgay.traThuong"));
			}
			
			if(importNewVO.getApplyToDate() != null && DateUtil.compareDateWithoutTime(importNewVO.getApplyFromDate(), importNewVO.getApplyToDate()) == 1) {
				importNewVO.setMessageError(R.getResource("common.compare.error.less.or.equal.tow.param", R.getResource("imp.epx.tuyen.clmn.tuNgay.traThuong"), R.getResource("imp.epx.tuyen.clmn.denNgay.traThuong")));
				return R.getResource("common.compare.error.less.or.equal.tow.param", R.getResource("imp.epx.tuyen.clmn.tuNgay.traThuong"), R.getResource("imp.epx.tuyen.clmn.denNgay.traThuong"));
			}
		}
		return "";
	}
	
	private String validateKMNew (PromotionImportNewVO importNewVO, int typePromotion) {
		if(importNewVO.getProductGroup() != null && importNewVO.getProductGroup().size() > 0) {
			if(importNewVO.getProductGroup().size() > 1 && !importNewVO.getType().equals(PromotionType.ZV09.getValue())) {
				importNewVO.setMessageError(R.getResource("ctkm.import.new.product.group.multi", importNewVO.getType()));
				return R.getResource("ctkm.import.new.product.group.multi", importNewVO.getType());
			}
			for(PromotionImportProductGroupNewVO groupNewVO : importNewVO.getProductGroup()) {
				if(groupNewVO.getGroupLevelBuys() != null && groupNewVO.getGroupLevelBuys().size() > 0) {
					for(int i = 0; i < groupNewVO.getGroupLevelBuys().size(); i++) {
						PromotionImportGroupLevelNewVO groupLevelBuy = groupNewVO.getGroupLevelBuys().get(i);
						if(!groupLevelBuy.hasParent()) {
							groupLevelBuy.setMessageError(R.getResource("ctkm.import.new.no.parent", groupLevelBuy.getGroupLevelCode()));
							return R.getResource("ctkm.import.new.no.parent", groupLevelBuy.getGroupLevelCode());
						}
						if(i < groupNewVO.getGroupLevelKMs().size()) {
							PromotionImportGroupLevelNewVO groupLevelKM = groupNewVO.getGroupLevelKMs().get(i);
							if(typePromotion == 1) {
								if(groupLevelKM.getAmount() == null) {
									groupLevelKM.setMessageError(R.getResource("ctkm.import.new.amount.null", groupLevelKM.getGroupLevelCode(), importNewVO.getPromotionCode()));
									return R.getResource("ctkm.import.new.amount.null", groupLevelKM.getGroupLevelCode(), importNewVO.getPromotionCode());
								}
								
								if(groupLevelKM.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
									groupLevelKM.setMessageError(R.getResource("ctkm.import.new.amount.zero", groupLevelKM.getGroupLevelCode(), importNewVO.getPromotionCode()));
									return R.getResource("ctkm.import.new.amount.zero", groupLevelKM.getGroupLevelCode(), importNewVO.getPromotionCode());
								}
							} else if(typePromotion == 2) {
								if(groupLevelKM.getPercent() == null) {
									groupLevelKM.setMessageError(R.getResource("ctkm.import.new.percent.null", groupLevelKM.getGroupLevelCode(), importNewVO.getPromotionCode()));
									return R.getResource("ctkm.import.new.percent.null", groupLevelKM.getGroupLevelCode(), importNewVO.getPromotionCode());
								}
								if(groupLevelKM.getPercent() < 0) {
									groupLevelKM.setMessageError(R.getResource("ctkm.import.new.percent.zero", groupLevelKM.getGroupLevelCode(), importNewVO.getPromotionCode()));
									return R.getResource("ctkm.import.new.percent.zero", groupLevelKM.getGroupLevelCode(), importNewVO.getPromotionCode());
								}
								if(groupLevelKM.getPercent() > 100) {
									groupLevelKM.setMessageError(R.getResource("ctkm.import.new.percent.zero", groupLevelKM.getGroupLevelCode(), importNewVO.getPromotionCode()));
									return R.getResource("ctkm.import.new.percent.zero", groupLevelKM.getGroupLevelCode(), importNewVO.getPromotionCode());
								}								
							} else if(typePromotion == 3) {
								if(groupLevelKM.getGroupLevelDetails() != null && groupLevelKM.getGroupLevelDetails().size() > 0) {
									for(int j = 0; j < groupLevelKM.getGroupLevelDetails().size(); j++) {
										if(groupLevelKM.getGroupLevelDetails().get(j).getQuantity() == null) {
											groupLevelKM.getGroupLevelDetails().get(j).setMessageError(R.getResource("ctkm.import.new.km.product.quantity.null", groupLevelKM.getGroupLevelCode(), importNewVO.getPromotionCode()));
											return R.getResource("ctkm.import.new.km.product.quantity.null", groupLevelKM.getGroupLevelCode(), importNewVO.getPromotionCode());
										}
									}
								} else {
									groupLevelKM.setMessageError(R.getResource("catalog.promotion.program.km.not.exists", groupLevelKM.getGroupLevelCode()));
									return R.getResource("catalog.promotion.program.km.not.exists", groupLevelKM.getGroupLevelCode());
								}
							}
						}						
					}
				}
			}
			
		}
		
		return "";
	}
	
	private String ValidateConditionKMCheckDupNew (List<List<String>> productCodeForLevels) {
		if(productCodeForLevels.size() > 0) {
			List<String> rootProductCodes = new ArrayList<>();
			rootProductCodes.addAll(productCodeForLevels.get(0));
			for (int i = 0; i < productCodeForLevels.size(); i++) {
				List<String> productCodes = productCodeForLevels.get(i);
				if(i != 0) {
					for(String code : productCodes) {
						if(rootProductCodes.indexOf(code) == -1) {
							return "ctkm.import.new.condition.product.is.not.homogeneous";
						}
					}
					
				}
			}
		}
		
		return "";
	}
	
	private String validateConditionKMNew(PromotionImportNewVO importNewVO, int promotionType) {
		final List<String> LIST_ZV_AMOUNT = Arrays.asList(PromotionType.ZV19.getValue(), PromotionType.ZV20.getValue(), PromotionType.ZV21.getValue());
		final List<String> LIST_REQUIRE_PRODUCT = Arrays.asList(PromotionType.ZV08.getValue(), PromotionType.ZV09.getValue(), PromotionType.ZV11.getValue(),PromotionType.ZV12.getValue());
		if(importNewVO.getProductGroup() != null && importNewVO.getProductGroup().size() > 0) {
			if(importNewVO.getProductGroup().size() > 1 && !importNewVO.getType().equals(PromotionType.ZV09.getValue())) {
				importNewVO.setMessageError(R.getResource("ctkm.import.new.product.group.multi", importNewVO.getType()));
				return R.getResource("ctkm.import.new.product.group.multi", importNewVO.getType());
			}
			
			for(PromotionImportProductGroupNewVO groupNewVO : importNewVO.getProductGroup()) {
				if(groupNewVO.getGroupLevelBuys() != null && groupNewVO.getGroupLevelBuys().size() > 0) {
					// duyet mức mua
					List<List<String>> productCodeForLevels = new ArrayList<>();
					List<Integer> units = new ArrayList<>();
					BigDecimal totalAmount;
					Integer totalQuantity;
					for(int i = 0; i < groupNewVO.getGroupLevelBuys().size(); i++) {						
						PromotionImportGroupLevelNewVO groupLevel = groupNewVO.getGroupLevelBuys().get(i);
						totalAmount = BigDecimal.ZERO;
						totalQuantity = 0;
						if (promotionType == 1) {
							if(groupLevel.getAmount() == null) {
								groupLevel.setMessageError(R.getResource("ctkm.import.new.condition.level.amount", importNewVO.getPromotionCode(), groupLevel.getGroupLevelCode()));
								return R.getResource("ctkm.import.new.condition.level.amount", importNewVO.getPromotionCode(), groupLevel.getGroupLevelCode());
							}
						} else {
							if(groupLevel.getQuantity()==null) {
								groupLevel.setMessageError(R.getResource("ctkm.import.new.condition.level.quantity", importNewVO.getPromotionCode(), groupLevel.getGroupLevelCode()));
								return R.getResource("ctkm.import.new.condition.level.quantity", importNewVO.getPromotionCode(), groupLevel.getGroupLevelCode());
							}
						}
						//lay danh sach DVT để kiem tra su dong nhat giua cac muc
						if(groupLevel.getQuantityUnit() > 0 && units.indexOf(groupLevel.getQuantityUnit()) == -1) {
							units.add(groupLevel.getQuantityUnit());
						}
						if(groupLevel.getGroupLevelDetails() != null && groupLevel.getGroupLevelDetails().size() > 0) {
							// duyet detail sp muc mua
							List<String> productCodes = new ArrayList<>();
							for(int j = 0; j < groupLevel.getGroupLevelDetails().size(); j++) {
								PromotionImportGroupLevelDetailNewVO detailNewVO = groupLevel.getGroupLevelDetails().get(j);
								if(!detailNewVO.isChild()){
									if(!StringUtil.isNullOrEmpty(detailNewVO.getProductCode())) {
										if(productCodes.indexOf(detailNewVO.getProductCode()) == -1) {
											productCodes.add(detailNewVO.getProductCode());
										} else {
											detailNewVO.setMessageError(R.getResource("ctkm.import.new.condition.level.product.diff.ex", importNewVO.getPromotionCode(), groupLevel.getGroupLevelCode()));
											return R.getResource("ctkm.import.new.condition.level.product.diff.ex", importNewVO.getPromotionCode(), groupLevel.getGroupLevelCode());
										}		
									}
	
									if(promotionType == 1) {
										if(detailNewVO.getAmount() != null){
											if (detailNewVO.getAmount().compareTo(groupLevel.getAmount()) == 1) {
												detailNewVO.setMessageError(R.getResource("ctkm.import.new.condition.level.detail.amount", importNewVO.getPromotionCode(), groupLevel.getGroupLevelCode()));
												return R.getResource("ctkm.import.new.condition.level.detail.amount", importNewVO.getPromotionCode(), groupLevel.getGroupLevelCode());
											}
											totalAmount = totalAmount.add(detailNewVO.getAmount());
										}
									} else {
										if (detailNewVO.getQuantity() != null){
											if(detailNewVO.getQuantity().compareTo(groupLevel.getQuantity()) == 1) {
												detailNewVO.setMessageError(R.getResource("ctkm.import.new.condition.level.detail.quantity", importNewVO.getPromotionCode(), groupLevel.getGroupLevelCode()));
												return R.getResource("ctkm.import.new.condition.level.detail.quantity", importNewVO.getPromotionCode(), groupLevel.getGroupLevelCode());
											}
											totalQuantity+= detailNewVO.getQuantity();
										}
									}
								}
								else
								{
									if(LIST_ZV_AMOUNT.contains(importNewVO.getType()))
									{
										if (promotionType == 1) {
											if(detailNewVO.getAmount() == null) {
												detailNewVO.setMessageError(R.getResource("ctkm.import.new.condition.level.amount.child", importNewVO.getPromotionCode(), groupLevel.getGroupLevelCode()));
												return R.getResource("ctkm.import.new.condition.level.amount.child", importNewVO.getPromotionCode(), groupLevel.getGroupLevelCode());
											}
											if(detailNewVO.getAmount().compareTo(groupLevel.getAmount())>0)
											{
												detailNewVO.setMessageError(R.getResource("common.compare.error.less.or.equal.tow.param","Số tiền mức con", "số tiền tối thiểu"));
												return R.getResource("common.compare.error.less.or.equal.tow.param","Số tiền mức con", "số tiền tối thiểu");
											}
										} else {
											if(detailNewVO.getQuantity()==null) {
												detailNewVO.setMessageError(R.getResource("ctkm.import.new.condition.level.quantity.child", importNewVO.getPromotionCode(), groupLevel.getGroupLevelCode()));
												return R.getResource("ctkm.import.new.condition.level.quantity.child", importNewVO.getPromotionCode(), groupLevel.getGroupLevelCode());
											}
											if(detailNewVO.getQuantity()>groupLevel.getQuantity())
											{
												detailNewVO.setMessageError(R.getResource("common.compare.error.less.or.equal.tow.param","Số lượng mức con", "số lượng tối thiểu"));
												return R.getResource("common.compare.error.less.or.equal.tow.param", "Số lượng mức con", "số lượng tối thiểu");
											}
										}									
									}
								}
								if(!detailNewVO.isChild()&&LIST_REQUIRE_PRODUCT.contains(importNewVO.getType())&&!detailNewVO.isRequired())
								{
									if(promotionType==1&&detailNewVO.getAmount() != null)
									{
									detailNewVO.setMessageError(R.getResource("ctkm.import.new.condition.level.amount.product.required", detailNewVO.getProductCode()));
									return R.getResource("ctkm.import.new.condition.level.amount.product.required", detailNewVO.getProductCode());
									}
									if(promotionType==2&&detailNewVO.getQuantity()!=null)
									{
									detailNewVO.setMessageError(R.getResource("ctkm.import.new.condition.level.quantity.product.required", detailNewVO.getProductCode()));
									return R.getResource("ctkm.import.new.condition.level.quantity.product.required", detailNewVO.getProductCode());
									}
								}
							}
							productCodeForLevels.add(productCodes);
						}
						if(promotionType==1)
						{
							if(groupLevel.getAmount().compareTo(totalAmount) < 0 )
							{
								groupLevel.setMessageError(R.getResource("common.compare.error.less.or.equal.tow.param","Tổng số tiền nhóm con","số tiền tối thiểu"));
								return R.getResource("common.compare.error.less.or.equal.tow.param","Tổng số tiền nhóm con","số tiền tối thiểu");
							}
						}
						else
						{
							if(groupLevel.getQuantity()< totalQuantity)
							{
								groupLevel.setMessageError(R.getResource("common.compare.error.less.or.equal.tow.param","Tổng số lượng SP nhóm con","số lượng tối thiểu"));
								return R.getResource("common.compare.error.less.or.equal.tow.param","Tổng số lượng SP nhóm con","số lượng tối thiểu");
							}
						}
					}

					// sort mức theo DS sản phảm
					Collections.sort(productCodeForLevels, new Comparator<List<String>>() {
						@Override
						public int compare(List<String> arr1, List<String> arr2) {
							if(arr1.size() > arr2.size()) {
								return -1;
							}
							if(arr1.size() < arr2.size()) {
								return 1;
							}
							return 0;
						}							
					});
					// kiem tra sp giữa các mức có đồng nhất với nhau?
					String messageError = ValidateConditionKMCheckDupNew(productCodeForLevels);
					if(!StringUtil.isNullOrEmpty(messageError)) {
						groupNewVO.setMessageError(R.getResource(messageError, importNewVO.getPromotionCode(), groupNewVO.getGroupCode()));
						return R.getResource(messageError, importNewVO.getPromotionCode(), groupNewVO.getGroupCode());
					}
					
					// kiem tra DVT giua cac muc có dong nhat với nhau?
					if(units.size() > 1) {
						groupNewVO.setMessageError(R.getResource("ctkm.import.new.condition.level.dvt", importNewVO.getPromotionCode(), groupNewVO.getGroupCode()));
						return R.getResource("ctkm.import.new.condition.level.dvt", importNewVO.getPromotionCode(), groupNewVO.getGroupCode());
					}
				}
			}
		}
		return "";
	}
	
	private String validateSubCondition(PromotionImportNewVO importNewVO, int promotionType) {
		if(importNewVO.getProductGroup() != null && importNewVO.getProductGroup().size() > 0) {
			// duyet nhom
			for(PromotionImportProductGroupNewVO groupNewVO : importNewVO.getProductGroup()) {
				if(groupNewVO.getGroupLevelBuys() != null && groupNewVO.getGroupLevelBuys().size() > 0) {
					//duyet muc
					for(int i = 0; i < groupNewVO.getGroupLevelBuys().size(); i++) {
						PromotionImportGroupLevelNewVO groupLevel = groupNewVO.getGroupLevelBuys().get(i);
						if(groupLevel.getGroupLevelDetails() != null && groupLevel.getGroupLevelDetails().size() > 0) {
							if(importNewVO.getType().equals(PromotionType.ZV19.getValue()) || importNewVO.getType().equals(PromotionType.ZV20.getValue()) || importNewVO.getType().equals(PromotionType.ZV21.getValue())) {
								List<String> lstProductCode = new ArrayList<>();
								// duyet SP mức
								for(int j = 0; j < groupLevel.getGroupLevelDetails().size() - 1; j++) {									
									PromotionImportGroupLevelDetailNewVO detailNewVOEx1 = groupLevel.getGroupLevelDetails().get(j);
									if(detailNewVOEx1.isChild()) {										
										if (lstProductCode.size() == 0) {
											lstProductCode.add(detailNewVOEx1.getProductCode());
										} else if(lstProductCode.indexOf(detailNewVOEx1.getProductCode()) != -1){
											detailNewVOEx1.setMessageError(R.getResource("ctkm.import.new.condition.level.detail.sub.condition.dupex1", importNewVO.getPromotionCode(), groupLevel.getGroupLevelCode()));
											return R.getResource("ctkm.import.new.condition.level.detail.sub.condition.dupex1", importNewVO.getPromotionCode(), groupLevel.getGroupLevelCode());
										}
										for(int k = j + 1; k < groupLevel.getGroupLevelDetails().size(); k++) {
											PromotionImportGroupLevelDetailNewVO detailNewVOEx2 = groupLevel.getGroupLevelDetails().get(k);
											if(detailNewVOEx2.isChild()) {
												if(detailNewVOEx1.getAmount() == null || detailNewVOEx1.getAmount().compareTo(BigDecimal.ZERO) < 1 ||
														detailNewVOEx2.getAmount() == null || detailNewVOEx2.getAmount().compareTo(BigDecimal.ZERO) < 1) {
													detailNewVOEx1.setMessageError(R.getResource("ctkm.import.new.condition.amount.sub.level.null", importNewVO.getPromotionCode(), groupLevel.getGroupLevelCode()));
													return R.getResource("ctkm.import.new.condition.amount.sub.level.null", importNewVO.getPromotionCode(), groupLevel.getGroupLevelCode());
												}
												
												if(!detailNewVOEx1.getAmount().equals(detailNewVOEx2.getAmount())) {
													detailNewVOEx1.setMessageError(R.getResource("ctkm.import.new.condition.amount.sub.level", importNewVO.getPromotionCode(), groupLevel.getGroupLevelCode()));
													return R.getResource("ctkm.import.new.condition.amount.sub.level", importNewVO.getPromotionCode(), groupLevel.getGroupLevelCode());
												}
												
												if(detailNewVOEx1.getAmount().compareTo(groupLevel.getAmount()) == 1) {
													detailNewVOEx1.setMessageError(R.getResource("ctkm.import.new.condition.level.detail.amount", importNewVO.getPromotionCode(), groupLevel.getGroupLevelCode()));
													return R.getResource("ctkm.import.new.condition.level.detail.amount", importNewVO.getPromotionCode(), groupLevel.getGroupLevelCode());
												}
											}
										}
									}
								}
							} else {
								Map<Integer, List<String>> subGroupLevelDetailQuantity = new HashMap<>();
								Map<BigDecimal, List<String>> subGroupLevelDetailAmount = new HashMap<>();
								List<String> productCodes = new ArrayList<>();
								List<String> productCodesForLevel = new ArrayList<>();
								List<String> productCodesForCheckDup = new ArrayList<>();
								createSubConditionForKM(groupLevel.getGroupLevelDetails(), promotionType, subGroupLevelDetailQuantity, subGroupLevelDetailAmount, productCodes, productCodesForLevel);
								if(productCodesForLevel.size() < productCodes.size()) {
									groupLevel.setMessageError(R.getResource("ctkm.import.new.condition.level.detail.sub.condition.product.size", importNewVO.getPromotionCode(), groupLevel.getGroupLevelCode()));
									return  R.getResource("ctkm.import.new.condition.level.detail.sub.condition.product.size", importNewVO.getPromotionCode(), groupLevel.getGroupLevelCode());
								}
								
								if(promotionType == 1) {
									for(Map.Entry<BigDecimal, List<String>> entry : subGroupLevelDetailAmount.entrySet()) {
										if(entry.getValue().size() < 2) {
											groupLevel.setMessageError(R.getResource("ctkm.import.new.condition.sub.level", importNewVO.getPromotionCode(), groupLevel.getGroupLevelCode()));
											return  R.getResource("ctkm.import.new.condition.sub.level", importNewVO.getPromotionCode(), groupLevel.getGroupLevelCode());
										}
										if(entry.getKey().compareTo(groupLevel.getAmount())==1)
										{
											groupLevel.setMessageError(R.getResource("common.compare.error.less.or.equal.tow.param","Số tiền của điều kiện con","số tiền tối thiểu"));
											return R.getResource("common.compare.error.less.or.equal.tow.param","Số tiền của điều kiện con","số tiền tối thiểu");
										}
										for(String productCode : entry.getValue()) {
											if(productCodesForLevel.indexOf(productCode) == -1) {
												groupLevel.setMessageError(R.getResource("ctkm.import.new.condition.level.detail.sub.condition.product.size", importNewVO.getPromotionCode(), groupLevel.getGroupLevelCode()));
												return  R.getResource("ctkm.import.new.condition.level.detail.sub.condition.product.size", importNewVO.getPromotionCode(), groupLevel.getGroupLevelCode());
											}
											
											if(productCodesForCheckDup.indexOf(productCode) == -1) {
												productCodesForCheckDup.add(productCode);
											} else {
												groupLevel.setMessageError(R.getResource("ctkm.import.new.condition.level.detail.sub.condition.dupex2", importNewVO.getPromotionCode(), groupLevel.getGroupLevelCode()));
												return R.getResource("ctkm.import.new.condition.level.detail.sub.condition.dupex2", importNewVO.getPromotionCode(), groupLevel.getGroupLevelCode());
											}
										}										
									}
								} else {
									for(Map.Entry<Integer, List<String>> entry : subGroupLevelDetailQuantity.entrySet()) {
										if(entry.getValue().size() < 2) {
											groupLevel.setMessageError(R.getResource("ctkm.import.new.condition.sub.level", importNewVO.getPromotionCode(), groupLevel.getGroupLevelCode()));
											return  R.getResource("ctkm.import.new.condition.level.detail.sub.condition.product.size", importNewVO.getPromotionCode(), groupLevel.getGroupLevelCode());
										}
										if(entry.getKey()>groupLevel.getQuantity())
										{
											groupLevel.setMessageError(R.getResource("common.compare.error.less.or.equal.tow.param","Số lượng của điều kiện con","số lượng tối thiểu"));
											return R.getResource("common.compare.error.less.or.equal.tow.param","Số lượng của điều kiện con","số lượng tối thiểu");
										}
										for(String productCode : entry.getValue()) {
											if(productCodesForLevel.indexOf(productCode) == -1) {
												groupLevel.setMessageError(R.getResource("ctkm.import.new.condition.level.detail.sub.condition.product.size", importNewVO.getPromotionCode(), groupLevel.getGroupLevelCode()));
												return  R.getResource("ctkm.import.new.condition.sub.level", importNewVO.getPromotionCode(), groupLevel.getGroupLevelCode());
											}
											
											if(productCodesForCheckDup.indexOf(productCode) == -1) {
												productCodesForCheckDup.add(productCode);
											} else {
												groupLevel.setMessageError(R.getResource("ctkm.import.new.condition.level.detail.sub.condition.dupex2", importNewVO.getPromotionCode(), groupLevel.getGroupLevelCode()));
												return R.getResource("ctkm.import.new.condition.level.detail.sub.condition.dupex2", importNewVO.getPromotionCode(), groupLevel.getGroupLevelCode());
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}
		return "";
	}
	
	private void convertObjectPromotionToCellBean(List<PromotionImportNewVO> importNewVOs, List<CellBean> infoPromotionError, List<CellBean> infoPromotionDetailError, List<CellBean> infoPromotionShopError) {
		for (PromotionImportNewVO importNewVO : importNewVOs) {
			// tao du lieu cho sheet thong tin chung
			CellBean sheet1 = new CellBean();
			sheet1.setContent1(!StringUtil.isNullOrEmpty(importNewVO.getPromotionCode()) ? importNewVO.getPromotionCode():"");
			sheet1.setContent2(!StringUtil.isNullOrEmpty(importNewVO.getPromotionName()) ? importNewVO.getPromotionName():"");
			sheet1.setContent3(!StringUtil.isNullOrEmpty(importNewVO.getVersion()) ? importNewVO.getVersion():"");
			sheet1.setContent4(!StringUtil.isNullOrEmpty(importNewVO.getType()) ? importNewVO.getType():"");
			sheet1.setContent5(!StringUtil.isNullOrEmpty(String.valueOf(importNewVO.getFromDate())) ? DateUtil.toDateString(importNewVO.getFromDate(), DateUtil.DATE_FORMAT_DDMMYYYY):"");
			sheet1.setContent6(!StringUtil.isNullOrEmpty(String.valueOf(importNewVO.getToDate())) ? DateUtil.toDateString(importNewVO.getToDate(), DateUtil.DATE_FORMAT_DDMMYYYY):"");
			sheet1.setContent7(!StringUtil.isNullOrEmpty(importNewVO.getNotice()) ? importNewVO.getNotice():"");
			sheet1.setContent8(!StringUtil.isNullOrEmpty(importNewVO.getNotice()) ? importNewVO.getDescriptionProduct():"");
			sheet1.setContent9(!StringUtil.isNullOrEmpty(importNewVO.getDescription()) ? importNewVO.getDescription():"");
			sheet1.setContent10(!StringUtil.isNullOrEmpty(String.valueOf(importNewVO.isMultiple())) ? String.valueOf(importNewVO.isMultiple() ? 1 : 0):"");
			sheet1.setContent11(!StringUtil.isNullOrEmpty(String.valueOf(importNewVO.isRecursive())) ? String.valueOf(importNewVO.isRecursive() ? 1 : 0):"");
			sheet1.setContent12(!StringUtil.isNullOrEmpty(String.valueOf(importNewVO.getRewardType())) ? String.valueOf(importNewVO.getRewardType()):"");
			sheet1.setContent13(!StringUtil.isNullOrEmpty(String.valueOf(importNewVO.getApplyFromDate())) ? DateUtil.toDateString(importNewVO.getApplyFromDate(), DateUtil.DATE_FORMAT_DDMMYYYY):"");
			sheet1.setContent14(!StringUtil.isNullOrEmpty(String.valueOf(importNewVO.getApplyToDate())) ? DateUtil.toDateString(importNewVO.getApplyToDate(), DateUtil.DATE_FORMAT_DDMMYYYY):"");
			sheet1.setErrMsg(importNewVO.getMessageError());
			infoPromotionError.add(sheet1);
			// tao du lieu cho sheet don vi tham gia
			for(PromotionImportShopNewVO importShopNewVO : importNewVO.getShops()) {
				CellBean sheet3 = new CellBean();
				sheet3.setContent1(!StringUtil.isNullOrEmpty(importNewVO.getPromotionCode()) ? importNewVO.getPromotionCode():"");
				sheet3.setContent2(!StringUtil.isNullOrEmpty(importShopNewVO.getShopCode()) ? importShopNewVO.getShopCode():"");
				sheet3.setContent3(importShopNewVO.getNum()!=null ? String.valueOf(importShopNewVO.getQuantity()):"");
				sheet3.setContent4(importShopNewVO.getAmount() != null ? StringUtil.convertMoney(importShopNewVO.getAmount()):"");
				sheet3.setContent5(importShopNewVO.getQuantity() !=null ? String.valueOf(importShopNewVO.getNum()):"");
				sheet3.setContent6(importShopNewVO.getMessageError());
				if(!StringUtil.isNullOrEmpty(importShopNewVO.getKeyMessage())) {
					sheet3.setErrMsg(R.getResource(importShopNewVO.getKeyMessage()));
				}
				
				infoPromotionShopError.add(sheet3);
			}
			
			//tao du lieu cho sheet co cau
			for(PromotionImportProductGroupNewVO groupNewVO:importNewVO.getProductGroup()) {
				for(int i = 0; i < groupNewVO.getGroupLevelBuys().size(); i++) {
					PromotionImportGroupLevelNewVO groupLevelNewMuaVO = groupNewVO.getGroupLevelBuys().get(i);
					PromotionImportGroupLevelNewVO groupLevelNewKMVO = groupNewVO.getGroupLevelKMs().get(i);
					// luu thong tin dòng tổng cơ cấu của một mức
					CellBean sheet2Ex1 = new CellBean();
					sheet2Ex1.setContent1(!StringUtil.isNullOrEmpty(importNewVO.getPromotionCode()) ? importNewVO.getPromotionCode():"");
					sheet2Ex1.setContent2(!StringUtil.isNullOrEmpty(importNewVO.getType()) ? importNewVO.getType():"");
					sheet2Ex1.setContent3(!StringUtil.isNullOrEmpty(groupNewVO.getGroupCode()) ? groupNewVO.getGroupCode():"");
					sheet2Ex1.setContent4(!StringUtil.isNullOrEmpty(groupLevelNewMuaVO.getGroupLevelCode()) ? groupLevelNewMuaVO.getGroupLevelCode():"");
					sheet2Ex1.setContent5("X");
					sheet2Ex1.setContent6("");
					sheet2Ex1.setContent7("");
					sheet2Ex1.setContent8(groupLevelNewMuaVO.getQuantity()!= null ? String.valueOf(groupLevelNewMuaVO.getQuantity()):"");
					sheet2Ex1.setContent9(!StringUtil.isNullOrEmpty(String.valueOf(groupLevelNewMuaVO.getQuantityUnit())) ? groupLevelNewMuaVO.getQuantityUnit()==1 ? R.getResource("ctkm.import.new.le"):R.getResource("ctkm.import.new.thung"):"");
					sheet2Ex1.setContent10(groupLevelNewMuaVO.getAmount()!=null ? String.valueOf(groupLevelNewMuaVO.getAmount()):"");
					sheet2Ex1.setContent11("");
					sheet2Ex1.setContent12(groupLevelNewKMVO.getAmount()!=null ? String.valueOf(groupLevelNewKMVO.getAmount()):"");
					sheet2Ex1.setContent13(groupLevelNewKMVO.getPercent()!=null ? String.valueOf(groupLevelNewKMVO.getPercent()):"");
					sheet2Ex1.setContent14("");
					sheet2Ex1.setContent15("");
					sheet2Ex1.setContent16("");
					sheet2Ex1.setContent17("");
					String muaError = StringUtil.isNullOrEmpty(groupLevelNewMuaVO.getMessageError())?"":groupLevelNewMuaVO.getMessageError();
					String kmError = StringUtil.isNullOrEmpty(groupLevelNewKMVO.getMessageError())?"":groupLevelNewKMVO.getMessageError();
					String groupErr = "";
					if(i==0)
					{
						groupErr =  groupNewVO.getMessageError()==null?"":groupNewVO.getMessageError();
					}
					sheet2Ex1.setErrMsg(groupErr+muaError+kmError);
					infoPromotionDetailError.add(sheet2Ex1);
					// lấy thong tin chi tiet cho từng mức
					
//					List<PromotionImportGroupLevelDetailNewVO> detailNewVOs;
					int size =0;
					if(groupLevelNewMuaVO.getGroupLevelDetails().size() >= groupLevelNewKMVO.getGroupLevelDetails().size()){
//						detailNewVOs = groupLevelNewMuaVO.getGroupLevelDetails();
						size = groupLevelNewMuaVO.getGroupLevelDetails().size();
					}else{
//						detailNewVOs = groupLevelNewKMVO.getGroupLevelDetails();
						size = groupLevelNewKMVO.getGroupLevelDetails().size();
					}
					
					for(int k = 0; k < size; k++) {
						CellBean sheet2Ex = new CellBean();
						PromotionImportGroupLevelDetailNewVO detailNewMuaVO = null;
						PromotionImportGroupLevelDetailNewVO detailNewKMVO = null;
						if(k < groupLevelNewMuaVO.getGroupLevelDetails().size()) {
							detailNewMuaVO = groupLevelNewMuaVO.getGroupLevelDetails().get(k);
						}
						
						if(k < groupLevelNewKMVO.getGroupLevelDetails().size()) {
							detailNewKMVO = groupLevelNewKMVO.getGroupLevelDetails().get(k);
						}
						
						// ghi thong tin chung
						sheet2Ex.setContent1(!StringUtil.isNullOrEmpty(importNewVO.getPromotionCode()) ? importNewVO.getPromotionCode():"");
						sheet2Ex.setContent2(!StringUtil.isNullOrEmpty(importNewVO.getType()) ? importNewVO.getType():"");
						sheet2Ex.setContent3(!StringUtil.isNullOrEmpty(groupNewVO.getGroupCode()) ? groupNewVO.getGroupCode():"");
						sheet2Ex.setContent4(!StringUtil.isNullOrEmpty(groupLevelNewMuaVO.getGroupLevelCode()) ? groupLevelNewMuaVO.getGroupLevelCode():"");
						sheet2Ex.setContent5("");
						if(detailNewMuaVO != null) {
							// ghi thong tin chi tiet muc mua
							if(detailNewMuaVO.isChild()){
								sheet2Ex.setContent6("X");
							}else{
								sheet2Ex.setContent6("");
							}
							sheet2Ex.setContent7(!StringUtil.isNullOrEmpty(detailNewMuaVO.getProductCode()) ? detailNewMuaVO.getProductCode() : "");
							sheet2Ex.setContent8(detailNewMuaVO.getQuantity()!=null ? String.valueOf(detailNewMuaVO.getQuantity()) : "");
							sheet2Ex.setContent9("");
							sheet2Ex.setContent10(detailNewMuaVO.getAmount()!=null ? String.valueOf(detailNewMuaVO.getAmount()) : "");
							if(detailNewMuaVO.isRequired()){
								sheet2Ex.setContent11("X");
							}else{
								sheet2Ex.setContent11("");
							}
							if(!StringUtil.isNullOrEmpty(detailNewMuaVO.getMessageError())){
								sheet2Ex.setErrMsg(detailNewMuaVO.getMessageError());
							}
							
						}
						if(detailNewKMVO != null) {
							// ghi thong tin chi tiet muc KM
							sheet2Ex.setContent12("");
							sheet2Ex.setContent13("");
							sheet2Ex.setContent14(!StringUtil.isNullOrEmpty(detailNewKMVO.getProductCode()) ? detailNewKMVO.getProductCode() : "");
							sheet2Ex.setContent15(detailNewKMVO.getQuantity()!=null ? String.valueOf(detailNewKMVO.getQuantity()) : "");
							sheet2Ex.setContent16("");
							if(detailNewKMVO.isRequired()){
								sheet2Ex.setContent17("X");
							}else{
								sheet2Ex.setContent17("");
							}
							if(!StringUtil.isNullOrEmpty(detailNewKMVO.getMessageError())){
								if (!StringUtil.isNullOrEmpty(sheet2Ex.getContent18())){
									sheet2Ex.setErrMsg(sheet2Ex.getContent18()+detailNewKMVO.getMessageError());
								}else{
									sheet2Ex.setErrMsg(detailNewKMVO.getMessageError());
								}
								
							}
						}
						infoPromotionDetailError.add(sheet2Ex);
					}
					
				}
			}
		}
	}
	
	/**
	 * validate nhóm KM:
	 * 	1: tiền
	 * 	2: %
	 * 	3: số lượng(sản phẩm)
	 * validate nhóm điều kiện đăng ký cha:
	 * 	1: tiền
	 * 	2: số lượng(sản phẩm)
	 * validate nhóm điều kiện đăng ký con:
	 * 	1: tiền
	 * 	2: số lượng(sản phẩm)
	 * */
	
	private String validateZV07New (PromotionImportNewVO importNewVO){
		String messageError = validateVoucherNew(importNewVO);
		if(!StringUtil.isNullOrEmpty(messageError)) {
			return messageError;
		}
		// validate KM tiền, % cho nhóm Mức KM
		messageError = validateKMNew(importNewVO, 2);
		if(!StringUtil.isNullOrEmpty(messageError)) {
			return messageError;
		}
		
		// validate số lượng cho nhóm điều kiện
		messageError = validateConditionKMNew(importNewVO, 2);
		if(!StringUtil.isNullOrEmpty(messageError)) {
			return messageError;
		}
		
		messageError = validateSubCondition(importNewVO, 2);
		if(!StringUtil.isNullOrEmpty(messageError)) {
			return messageError;
		}
		return "";
	}
	
	private String validateZV08New (PromotionImportNewVO importNewVO){
		String messageError = validateVoucherNew(importNewVO);
		if(!StringUtil.isNullOrEmpty(messageError)) {
			return messageError;
		}
		
		// validate KM tiền, % cho nhóm Mức KM
		messageError = validateKMNew (importNewVO, 1);
		if(!StringUtil.isNullOrEmpty(messageError)) {
			return messageError;
		}
		
		// validate số lượng cho nhóm điều kiện
		messageError = validateConditionKMNew(importNewVO, 2);
		if(!StringUtil.isNullOrEmpty(messageError)) {
			return messageError;
		}
		
		messageError = validateSubCondition(importNewVO, 2);
		if(!StringUtil.isNullOrEmpty(messageError)) {
			return messageError;
		}
		return "";
	}
	
	private String validateZV09New (PromotionImportNewVO importNewVO){
		// validate product quantity cho nhóm Mức KM
		String messageError = validateKMNew(importNewVO, 3);
		if(!StringUtil.isNullOrEmpty(messageError)) {
			return messageError;
		}
		
		// validate số lượng cho nhóm điều kiện
		messageError = validateConditionKMNew(importNewVO, 2);
		if(!StringUtil.isNullOrEmpty(messageError)) {
			return messageError;
		}
		
		messageError = validateSubCondition(importNewVO, 2);
		if(!StringUtil.isNullOrEmpty(messageError)) {
			return messageError;
		}
		return "";
	}
	
	private String validateZV10New (PromotionImportNewVO importNewVO){
		String messageError = validateVoucherNew(importNewVO);
		if(!StringUtil.isNullOrEmpty(messageError)) {
			return messageError;
		}
		
		// validate KM tiền, % cho nhóm Mức KM
		messageError = validateKMNew (importNewVO, 2);
		if(!StringUtil.isNullOrEmpty(messageError)) {
			return messageError;
		}
		
		// validate số tiền cho nhóm điều kiện
		messageError = validateConditionKMNew(importNewVO, 1);
		if(!StringUtil.isNullOrEmpty(messageError)) {
			return messageError;
		}
		
		messageError = validateSubCondition(importNewVO, 1);
		if(!StringUtil.isNullOrEmpty(messageError)) {
			return messageError;
		}
		return "";
	}
	
	private String validateZV11New (PromotionImportNewVO importNewVO){
		String messageError = validateVoucherNew(importNewVO);
		if(!StringUtil.isNullOrEmpty(messageError)) {
			return messageError;
		}
		
		// validate KM tiền, % cho nhóm Mức KM
		messageError = validateKMNew (importNewVO, 1);
		if(!StringUtil.isNullOrEmpty(messageError)) {
			return messageError;
		}
		
		// validate số tiền cho nhóm điều kiện
		messageError = validateConditionKMNew(importNewVO, 1);
		if(!StringUtil.isNullOrEmpty(messageError)) {
			return messageError;
		}
		
		messageError = validateSubCondition(importNewVO, 1);
		if(!StringUtil.isNullOrEmpty(messageError)) {
			return messageError;
		}
		return "";
	}
	
	private String validateZV12New (PromotionImportNewVO importNewVO){
		// validate product quantity cho nhóm Mức KM
		String messageError = validateKMNew(importNewVO, 3);
		if(!StringUtil.isNullOrEmpty(messageError)) {
			return messageError;
		}
		
		// validate số tiền cho nhóm điều kiện
		messageError = validateConditionKMNew(importNewVO, 1);
		if(!StringUtil.isNullOrEmpty(messageError)) {
			return messageError;
		}
		
		messageError = validateSubCondition(importNewVO, 1);
		if(!StringUtil.isNullOrEmpty(messageError)) {
			return messageError;
		}
		return "";
	}
	
	private String validateZV19New (PromotionImportNewVO importNewVO){
		String messageError = validateVoucherNew(importNewVO);
		if(!StringUtil.isNullOrEmpty(messageError)) {
			return messageError;
		}
		
		// validate KM tiền, % cho nhóm Mức KM
		messageError = validateKMNew (importNewVO, 2);
		if(!StringUtil.isNullOrEmpty(messageError)) {
			return messageError;
		}
		
		// validate số tiền cho nhóm điều kiện
		messageError = validateConditionKMNew(importNewVO, 1);
		if(!StringUtil.isNullOrEmpty(messageError)) {
			return messageError;
		}
		
		messageError = validateSubCondition(importNewVO, 1);
		if(!StringUtil.isNullOrEmpty(messageError)) {
			return messageError;
		}
		return "";
	}
	
	private String validateZV20New (PromotionImportNewVO importNewVO){
		String messageError = validateVoucherNew(importNewVO);
		if(!StringUtil.isNullOrEmpty(messageError)) {
			return messageError;
		}
		
		// validate KM tiền, % cho nhóm Mức KM
		messageError = validateKMNew(importNewVO, 1);
		if(!StringUtil.isNullOrEmpty(messageError)) {
			return messageError;
		}
		
		// validate số tiền cho nhóm điều kiện
		messageError = validateConditionKMNew(importNewVO, 1);
		if(!StringUtil.isNullOrEmpty(messageError)) {
			return messageError;
		}
		
		messageError = validateSubCondition(importNewVO, 1);
		if(!StringUtil.isNullOrEmpty(messageError)) {
			return messageError;
		}
		return "";
	}
	
	private String validateZV21New (PromotionImportNewVO importNewVO){
		// validate product quantity cho nhóm Mức KM
		String messageError = validateKMNew(importNewVO, 3);
		if(!StringUtil.isNullOrEmpty(messageError)) {
			return messageError;
		}
		
		// validate số tiền cho nhóm điều kiện
		messageError = validateConditionKMNew(importNewVO, 1);
		if(!StringUtil.isNullOrEmpty(messageError)) {
			return messageError;
		}
		
		messageError = validateSubCondition(importNewVO, 1);
		if(!StringUtil.isNullOrEmpty(messageError)) {
			return messageError;
		}
		return "";
	}
	


	/**
	 * Import Chuong trinh khuyen mai
	 * 
	 * @modify hunglm16
	 * @return
	 * @throws Exception
	 * @since 10/09/2015
	 */
	public String importExcel() throws Exception {
		resetToken(result);
		//Kiem tra tap tin hop le
		errMsg = ValidateUtil.validateExcelFile(excelFile, excelFileContentType);
		if (!StringUtil.isNullOrEmpty(errMsg)) {
			isError = true;
			return SUCCESS;
		}
		return importExcel21ZV();
	}

	private String importExcel21ZV() throws Exception {
		actionStartTime = new Date();
		/**
		 * get data for listHeader, listDetail,, mapPromotionKM, mapMuaKM
		 */
		final int NUM_SHEETS = 3;
		try {
			getExcelData21ZV();
			for (String promotionCode : mapErrorPromotion.keySet()) {
				for (int i = 0; i < listHeader.size(); i++) {
					ExcelPromotionHeader header = listHeader.get(i);
					if (!StringUtil.isNullOrEmpty(header.promotionCode) && header.promotionCode.equals(promotionCode)) {
						listHeader.remove(i);
						totalItem = totalItem > 0 ? totalItem - 1 : totalItem;
					}
				}
				mapHeader.remove(promotionCode);
				mapPromotionMua.remove(promotionCode);
				mapPromotionKM.remove(promotionCode);
				mapType.remove(promotionCode);
			}
			String messageError = "";
			for (String promotionCode : mapPromotionMua.keySet()) {
				ListGroupMua listGroupMua = mapPromotionMua.get(promotionCode);
				ListGroupKM listGroupKM = mapPromotionKM.get(promotionCode);
				if (PromotionType.ZV01.getValue().equals(mapType.get(promotionCode))) {
					messageError = validateZV01(promotionCode, listGroupMua, listGroupKM);
				} else if (PromotionType.ZV02.getValue().equals(mapType.get(promotionCode))) {
					messageError = validateZV02(promotionCode, listGroupMua, listGroupKM);
				} else if (PromotionType.ZV03.getValue().equals(mapType.get(promotionCode))) {
					messageError = validateZV03(promotionCode, listGroupMua, listGroupKM);
				} else if (PromotionType.ZV04.getValue().equals(mapType.get(promotionCode))) {
					messageError = validateZV04(promotionCode, listGroupMua, listGroupKM);
				} else if (PromotionType.ZV05.getValue().equals(mapType.get(promotionCode))) {
					messageError = validateZV05(promotionCode, listGroupMua, listGroupKM);
				} else if (PromotionType.ZV06.getValue().equals(mapType.get(promotionCode))) {
					messageError = validateZV06(promotionCode, listGroupMua, listGroupKM);
				} else if (PromotionType.ZV07.getValue().equals(mapType.get(promotionCode))) {
					messageError = validateZV07(promotionCode, listGroupMua, listGroupKM);
				} else if (PromotionType.ZV08.getValue().equals(mapType.get(promotionCode))) {
					messageError = validateZV08(promotionCode, listGroupMua, listGroupKM);
				} else if (PromotionType.ZV09.getValue().equals(mapType.get(promotionCode))) {
					messageError = validateZV09(promotionCode, listGroupMua, listGroupKM);
				} else if (PromotionType.ZV10.getValue().equals(mapType.get(promotionCode))) {
					messageError = validateZV10(promotionCode, listGroupMua, listGroupKM);
				} else if (PromotionType.ZV11.getValue().equals(mapType.get(promotionCode))) {
					messageError = validateZV11(promotionCode, listGroupMua, listGroupKM);
				} else if (PromotionType.ZV12.getValue().equals(mapType.get(promotionCode))) {
					messageError = validateZV12(promotionCode, listGroupMua, listGroupKM);
				} else if (PromotionType.ZV13.getValue().equals(mapType.get(promotionCode))) {
					messageError = validateZV13(promotionCode, listGroupMua, listGroupKM);
				} else if (PromotionType.ZV14.getValue().equals(mapType.get(promotionCode))) {
					messageError = validateZV14(promotionCode, listGroupMua, listGroupKM);
				} else if (PromotionType.ZV15.getValue().equals(mapType.get(promotionCode))) {
					messageError = validateZV15(promotionCode, listGroupMua, listGroupKM);
				} else if (PromotionType.ZV16.getValue().equals(mapType.get(promotionCode))) {
					messageError = validateZV16(promotionCode, listGroupMua, listGroupKM);
				} else if (PromotionType.ZV17.getValue().equals(mapType.get(promotionCode))) {
					messageError = validateZV17(promotionCode, listGroupMua, listGroupKM);
				} else if (PromotionType.ZV18.getValue().equals(mapType.get(promotionCode))) {
					messageError = validateZV18(promotionCode, listGroupMua, listGroupKM);
				} else if (PromotionType.ZV19.getValue().equals(mapType.get(promotionCode))) {
					messageError = validateZV19(promotionCode, listGroupMua, listGroupKM);
				} else if (PromotionType.ZV20.getValue().equals(mapType.get(promotionCode))) {
					messageError = validateZV20(promotionCode, listGroupMua, listGroupKM);
				} else if (PromotionType.ZV21.getValue().equals(mapType.get(promotionCode))) {
					messageError = validateZV21(promotionCode, listGroupMua, listGroupKM);
				} else if (PromotionType.ZV22.getValue().equals(mapType.get(promotionCode))) {
					messageError = validateZV22(promotionCode, listGroupMua, listGroupKM);
				} else if (PromotionType.ZV23.getValue().equals(mapType.get(promotionCode))) {
					messageError = validateZV23(promotionCode, listGroupMua, listGroupKM);
				} else if (PromotionType.ZV24.getValue().equals(mapType.get(promotionCode))) {
					messageError = validateZV24(promotionCode, listGroupMua, listGroupKM);
				}
			}
			for (ExcelPromotionHeader header : listHeader) {
				if (mapPromotionMua.get(header.promotionCode) == null || mapPromotionKM.get(header.promotionCode) == null) {
					messageError = Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "catalog.promotion.import.header.not.has.detail", header.promotionCode);
					break;
				}
			}

			if (!StringUtil.isNullOrEmpty(messageError)) {
				errMsg = messageError;
				isError = true;
				return SUCCESS;
			} else {
				fileNameFail = exportFail();
				if (StringUtil.isNullOrEmpty(fileNameFail)) {
					String userName = null;
					if(currentUser != null){
						userName = currentUser.getUserName();
					}
					promotionProgramMgr.importPromotionVNM(userName,listDetail, listUnit, listHeader, mapPromotionMua, mapPromotionKM, mapMuaKM, lstPromotionShop, getLogInfoVO());
					for (ExcelPromotionHeader header : listHeader) {
						PromotionProgram pp = promotionProgramMgr.getPromotionProgramByCode(header.promotionCode.toUpperCase());
						if (pp != null) {
							promotionProgramMgr.updateMD5ValidCode(pp, getLogInfoVO());
						}
					}
				}
			}

		} catch (Exception e) {
			if (ERR_NUM_SHEET.equals(e.getMessage())) {
				errMsg = R.getResource("catalog.promotion.import.num.sheet.error", NUM_SHEETS);
			} else {
				errMsg = Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "system.error");
				LogUtility.logErrorStandard(e, R.getResource("web.log.message.error", "vnm.web.action.program.PromotionCatalogAction.importExcel21ZV"), createLogErrorStandard(actionStartTime));
			}
		} finally {
			// TODO: don dep resource map, list, ...
		}
		if (StringUtil.isNullOrEmpty(errMsg)) {
			isError = false;
		} else {
			isError = true;
		}
		//		System.gc();
		return SUCCESS;
	}

	public boolean checkMatch2Level(GroupSP preLevel, String productCode) {
		boolean isMatchProduct = false;
		for (int k = 0; k < preLevel.lstSP.size(); k++) {
			if (preLevel.lstSP.get(k).productCode.equals(productCode)) {
				isMatchProduct = true;
				break;
			}
		}
		return isMatchProduct;
	}

	public String validateZV01(String promotionCode, ListGroupMua listGroupMua, ListGroupKM listGroupKM) {
		String errorMessage = validateLineQuantity(promotionCode, listGroupMua);
		if (!StringUtil.isNullOrEmpty(errorMessage)) {
			return errorMessage;
		}
		errorMessage = validateLineQuantityUnit(promotionCode, listGroupMua);
		if (!StringUtil.isNullOrEmpty(errorMessage)) {
			return errorMessage;
		}
		errorMessage = validateFreePercent(promotionCode, listGroupKM);
		return validateCountLevel(errorMessage, promotionCode, listGroupMua, listGroupKM, PERCENT);
	}

	public String validateZV02(String promotionCode, ListGroupMua listGroupMua, ListGroupKM listGroupKM) {
		String errorMessage = validateLineQuantity(promotionCode, listGroupMua);
		if (!StringUtil.isNullOrEmpty(errorMessage)) {
			return errorMessage;
		}
		errorMessage = validateLineQuantityUnit(promotionCode, listGroupMua);
		if (!StringUtil.isNullOrEmpty(errorMessage)) {
			return errorMessage;
		}
		errorMessage = validateFreeAmount(promotionCode, listGroupKM);
		return validateCountLevel(errorMessage, promotionCode, listGroupMua, listGroupKM, AMOUNT);
	}

	public String validateZV03(String promotionCode, ListGroupMua listGroupMua, ListGroupKM listGroupKM) {
		String errorMessage = validateLineQuantity(promotionCode, listGroupMua);
		if (!StringUtil.isNullOrEmpty(errorMessage)) {
			return errorMessage;
		}
		errorMessage = validateLineQuantityUnit(promotionCode, listGroupMua);
		if (!StringUtil.isNullOrEmpty(errorMessage)) {
			return errorMessage;
		}
		errorMessage = validateFreeItem(promotionCode, listGroupKM);
		return validateCountLevel(errorMessage, promotionCode, listGroupMua, listGroupKM, QUANTITY);
	}

	public String validateZV04(String promotionCode, ListGroupMua listGroupMua, ListGroupKM listGroupKM) {
		String errorMessage = validateLineAmount(promotionCode, listGroupMua);
		if (!StringUtil.isNullOrEmpty(errorMessage)) {
			return errorMessage;
		}
		errorMessage = validateFreePercent(promotionCode, listGroupKM);
		return validateCountLevel(errorMessage, promotionCode, listGroupMua, listGroupKM, PERCENT);
	}

	public String validateZV05(String promotionCode, ListGroupMua listGroupMua, ListGroupKM listGroupKM) {
		String errorMessage = validateLineAmount(promotionCode, listGroupMua);
		if (!StringUtil.isNullOrEmpty(errorMessage)) {
			return errorMessage;
		}
		errorMessage = validateFreeAmount(promotionCode, listGroupKM);
		return validateCountLevel(errorMessage, promotionCode, listGroupMua, listGroupKM, AMOUNT);
	}

	public String validateZV06(String promotionCode, ListGroupMua listGroupMua, ListGroupKM listGroupKM) {
		String errorMessage = validateLineAmount(promotionCode, listGroupMua);
		if (!StringUtil.isNullOrEmpty(errorMessage)) {
			return errorMessage;
		}
		errorMessage = validateFreeItem(promotionCode, listGroupKM);
		return validateCountLevel(errorMessage, promotionCode, listGroupMua, listGroupKM, QUANTITY);
	}

	public String validateZV07(String promotionCode, ListGroupMua listGroupMua, ListGroupKM listGroupKM) {
		String errorMessage = validateGroupQuantity(promotionCode, listGroupMua);
		if (!StringUtil.isNullOrEmpty(errorMessage)) {
			return errorMessage;
		}
		errorMessage = validateGroupQuantityUnit(promotionCode, listGroupMua);
		if (!StringUtil.isNullOrEmpty(errorMessage)) {
			return errorMessage;
		}
		errorMessage = validateFreePercent(promotionCode, listGroupKM);
		return validateCountLevel(errorMessage, promotionCode, listGroupMua, listGroupKM, PERCENT);
	}

	public String validateZV08(String promotionCode, ListGroupMua listGroupMua, ListGroupKM listGroupKM) {
		String errorMessage = validateGroupQuantity(promotionCode, listGroupMua);
		if (!StringUtil.isNullOrEmpty(errorMessage)) {
			return errorMessage;
		}
		errorMessage = validateGroupQuantityUnit(promotionCode, listGroupMua);
		if (!StringUtil.isNullOrEmpty(errorMessage)) {
			return errorMessage;
		}
		errorMessage = validateFreeAmount(promotionCode, listGroupKM);
		return validateCountLevel(errorMessage, promotionCode, listGroupMua, listGroupKM, AMOUNT);
	}

	public String validateZV09(String promotionCode, ListGroupMua listGroupMua, ListGroupKM listGroupKM) {
		String errorMessage = validateGroupQuantity(promotionCode, listGroupMua);
		if (!StringUtil.isNullOrEmpty(errorMessage)) {
			return errorMessage;
		}
		errorMessage = validateGroupQuantityUnit(promotionCode, listGroupMua);
		if (!StringUtil.isNullOrEmpty(errorMessage)) {
			return errorMessage;
		}
		errorMessage = validateFreeItem(promotionCode, listGroupKM);
		return validateCountLevel(errorMessage, promotionCode, listGroupMua, listGroupKM, QUANTITY);
	}

	public String validateZV10(String promotionCode, ListGroupMua listGroupMua, ListGroupKM listGroupKM) {
		String errorMessage = validateGroupAmount(promotionCode, listGroupMua);
		if (!StringUtil.isNullOrEmpty(errorMessage)) {
			return errorMessage;
		}
		errorMessage = validateFreePercent(promotionCode, listGroupKM);
		return validateCountLevel(errorMessage, promotionCode, listGroupMua, listGroupKM, PERCENT);
	}

	public String validateZV11(String promotionCode, ListGroupMua listGroupMua, ListGroupKM listGroupKM) {
		String errorMessage = validateGroupAmount(promotionCode, listGroupMua);
		if (!StringUtil.isNullOrEmpty(errorMessage)) {
			return errorMessage;
		}
		errorMessage = validateFreeAmount(promotionCode, listGroupKM);
		return validateCountLevel(errorMessage, promotionCode, listGroupMua, listGroupKM, AMOUNT);
	}

	public String validateZV12(String promotionCode, ListGroupMua listGroupMua, ListGroupKM listGroupKM) {
		String errorMessage = validateGroupAmount(promotionCode, listGroupMua);
		if (!StringUtil.isNullOrEmpty(errorMessage)) {
			return errorMessage;
		}
		errorMessage = validateFreeItem(promotionCode, listGroupKM);
		return validateCountLevel(errorMessage, promotionCode, listGroupMua, listGroupKM, QUANTITY);
	}

	public String validateZV13(String promotionCode, ListGroupMua listGroupMua, ListGroupKM listGroupKM) {
		String errorMessage = validateBundleQuantity(promotionCode, listGroupMua);
		if (!StringUtil.isNullOrEmpty(errorMessage)) {
			return errorMessage;
		}
		errorMessage = validateBundleQuantityUnit(promotionCode, listGroupMua);
		if (!StringUtil.isNullOrEmpty(errorMessage)) {
			return errorMessage;
		}
		errorMessage = validateFreePercent(promotionCode, listGroupKM);
		return validateCountLevel(errorMessage, promotionCode, listGroupMua, listGroupKM, PERCENT);
	}

	public String validateZV14(String promotionCode, ListGroupMua listGroupMua, ListGroupKM listGroupKM) {
		String errorMessage = validateBundleQuantity(promotionCode, listGroupMua);
		if (!StringUtil.isNullOrEmpty(errorMessage)) {
			return errorMessage;
		}
		errorMessage = validateBundleQuantityUnit(promotionCode, listGroupMua);
		if (!StringUtil.isNullOrEmpty(errorMessage)) {
			return errorMessage;
		}
		errorMessage = validateFreeAmount(promotionCode, listGroupKM);
		return validateCountLevel(errorMessage, promotionCode, listGroupMua, listGroupKM, AMOUNT);
	}

	public String validateZV15(String promotionCode, ListGroupMua listGroupMua, ListGroupKM listGroupKM) {
		String errorMessage = validateBundleQuantity(promotionCode, listGroupMua);
		if (!StringUtil.isNullOrEmpty(errorMessage)) {
			return errorMessage;
		}
		errorMessage = validateBundleQuantityUnit(promotionCode, listGroupMua);
		if (!StringUtil.isNullOrEmpty(errorMessage)) {
			return errorMessage;
		}
		errorMessage = validateFreeItem(promotionCode, listGroupKM);
		return validateCountLevel(errorMessage, promotionCode, listGroupMua, listGroupKM, QUANTITY);
	}

	public String validateZV16(String promotionCode, ListGroupMua listGroupMua, ListGroupKM listGroupKM) {
		String errorMessage = validateBundleAmount(promotionCode, listGroupMua);
		if (!StringUtil.isNullOrEmpty(errorMessage)) {
			return errorMessage;
		}
		errorMessage = validateFreePercent(promotionCode, listGroupKM);
		return validateCountLevel(errorMessage, promotionCode, listGroupMua, listGroupKM, PERCENT);
	}

	public String validateZV17(String promotionCode, ListGroupMua listGroupMua, ListGroupKM listGroupKM) {
		String errorMessage = validateBundleAmount(promotionCode, listGroupMua);
		if (!StringUtil.isNullOrEmpty(errorMessage)) {
			return errorMessage;
		}
		errorMessage = validateFreeAmount(promotionCode, listGroupKM);
		return validateCountLevel(errorMessage, promotionCode, listGroupMua, listGroupKM, AMOUNT);
	}

	public String validateZV18(String promotionCode, ListGroupMua listGroupMua, ListGroupKM listGroupKM) {
		String errorMessage = validateBundleAmount(promotionCode, listGroupMua);
		if (!StringUtil.isNullOrEmpty(errorMessage)) {
			return errorMessage;
		}
		errorMessage = validateFreeItem(promotionCode, listGroupKM);
		return validateCountLevel(errorMessage, promotionCode, listGroupMua, listGroupKM, QUANTITY);
	}

	public String validateZV19(String promotionCode, ListGroupMua listGroupMua, ListGroupKM listGroupKM) {
		String errorMessage = validateAmount(promotionCode, listGroupMua);
		if (!StringUtil.isNullOrEmpty(errorMessage)) {
			return errorMessage;
		}
		errorMessage = validateFreePercent(promotionCode, listGroupKM);
		return validateCountLevel(errorMessage, promotionCode, listGroupMua, listGroupKM, PERCENT);
	}

	public String validateZV20(String promotionCode, ListGroupMua listGroupMua, ListGroupKM listGroupKM) {
		String errorMessage = validateAmount(promotionCode, listGroupMua);
		if (!StringUtil.isNullOrEmpty(errorMessage)) {
			return errorMessage;
		}
		errorMessage = validateFreeAmount(promotionCode, listGroupKM);
		return validateCountLevel(errorMessage, promotionCode, listGroupMua, listGroupKM, AMOUNT);
	}

	public String validateZV21(String promotionCode, ListGroupMua listGroupMua, ListGroupKM listGroupKM) {
		String errorMessage = validateAmount(promotionCode, listGroupMua);
		if (!StringUtil.isNullOrEmpty(errorMessage)) {
			return errorMessage;
		}
		errorMessage = validateFreeItem(promotionCode, listGroupKM);
		return validateCountLevel(errorMessage, promotionCode, listGroupMua, listGroupKM, QUANTITY);
	}

	public String validateZV22(String promotionCode, ListGroupMua listGroupMua, ListGroupKM listGroupKM) {
		String errorMessage;
		errorMessage = validateQuantity(promotionCode, listGroupMua);
		if (!StringUtil.isNullOrEmpty(errorMessage)) {
			return errorMessage;
		}
		errorMessage = validateQuantityUnit(promotionCode, listGroupMua);
		if (!StringUtil.isNullOrEmpty(errorMessage)) {
			return errorMessage;
		}
		errorMessage = validateFreePercent(promotionCode, listGroupKM);
		return validateCountLevel(errorMessage, promotionCode, listGroupMua, listGroupKM, PERCENT);
	}

	public String validateZV23(String promotionCode, ListGroupMua listGroupMua, ListGroupKM listGroupKM) {
		String errorMessage;
		errorMessage = validateQuantity(promotionCode, listGroupMua);
		if (!StringUtil.isNullOrEmpty(errorMessage)) {
			return errorMessage;
		}
		errorMessage = validateQuantityUnit(promotionCode, listGroupMua);
		if (!StringUtil.isNullOrEmpty(errorMessage)) {
			return errorMessage;
		}
		errorMessage = validateFreeAmount(promotionCode, listGroupKM);
		return validateCountLevel(errorMessage, promotionCode, listGroupMua, listGroupKM, AMOUNT);
	}

	public String validateZV24(String promotionCode, ListGroupMua listGroupMua, ListGroupKM listGroupKM) {
		String errorMessage;
		errorMessage = validateQuantity(promotionCode, listGroupMua);
		if (!StringUtil.isNullOrEmpty(errorMessage)) {
			return errorMessage;
		}
		errorMessage = validateQuantityUnit(promotionCode, listGroupMua);
		if (!StringUtil.isNullOrEmpty(errorMessage)) {
			return errorMessage;
		}
		errorMessage = validateFreeItem(promotionCode, listGroupKM);
		return validateCountLevel(errorMessage, promotionCode, listGroupMua, listGroupKM, QUANTITY);
	}

	public String validateZV25(String promotionCode, ListGroupMua listGroupMua, ListGroupKM listGroupKM) {
		String errorMessage = "";
		return errorMessage;
	}

	public String validateZV26(String promotionCode, ListGroupMua listGroupMua, ListGroupKM listGroupKM) {
		String errorMessage = "";
		return errorMessage;
	}

	public String validateCountLevel(String errorMessage, String promotionCode, ListGroupMua listGroupMua, ListGroupKM listGroupKM, String type) {
		if (!StringUtil.isNullOrEmpty(errorMessage)) {
			return errorMessage;
		}
		Integer countLevelMua = 0;
		Integer countLevelKM = 0;
		if (listGroupMua != null) {
			for (GroupMua gm : listGroupMua) {
				if (gm.lstLevel != null) {
					countLevelMua += gm.lstLevel.size();
				}
			}
		}
		if (listGroupKM != null) {
			for (GroupKM gkm : listGroupKM) {
				if (gkm.lstLevel != null) {
					countLevelKM += gkm.lstLevel.size();
				}
			}
		}
		if (!countLevelMua.equals(countLevelKM)) {
			if (QUANTITY.equals(type)) {
				errorMessage = R.getResource("catalog.promotion.import.type.zv09.free.quantity.not.same", promotionCode);
			} else if (AMOUNT.equals(type)) {
				errorMessage = R.getResource("catalog.promotion.import.type.zv09.amount.not.same", promotionCode);
			} else {
				errorMessage = R.getResource("catalog.promotion.import.type.zv09.percent.not.same", promotionCode);
			}
		}
		//SPMua
		if (StringUtil.isNullOrEmpty(errorMessage)) {
			//kiem tra cac mức có andor giong nhau chua
			boolean isErrAndOr = false;
			if (listGroupMua != null) {
				for (GroupMua gm : listGroupMua) {
					if (gm.lstLevel != null && gm.lstLevel.size() > 0) {
						for (GroupSP groupSP : gm.lstLevel) {
							if (groupSP.lstSP != null && groupSP.lstSP.size() > 0) {
								Boolean isRequired = groupSP.lstSP.get(0).isRequired;
								if (isRequired != null) {
									for (Node node : groupSP.lstSP) {
										if (!isRequired.equals(node.isRequired)) {
											isErrAndOr = true;
											break;
										}
									}
									if (isErrAndOr) {
										break;
									}
								}
							}
						}
						if (isErrAndOr) {
							break;
						}
					}
				}
				if (isErrAndOr) {
					errorMessage = R.getResource("catalog.promotion.import.andor.not.match.in.level", promotionCode);
				}
			}
		}

		//SPKM
		if (StringUtil.isNullOrEmpty(errorMessage)) {
			//kiem tra cac mức có andor giong nhau chua
			boolean isErrAndOr = false;
			if (listGroupKM != null) {
				for (GroupKM gkm : listGroupKM) {
					if (gkm.lstLevel != null && gkm.lstLevel.size() > 0) {
						for (GroupSP groupSP : gkm.lstLevel) {
							if (groupSP.lstSP != null && groupSP.lstSP.size() > 0) {
								Boolean isRequired = groupSP.lstSP.get(0).isRequired;
								if (isRequired != null) {
									for (Node node : groupSP.lstSP) {
										if (!isRequired.equals(node.isRequired)) {
											isErrAndOr = true;
											break;
										}
									}
									if (isErrAndOr) {
										break;
									}
								}
							}
						}
						if (isErrAndOr) {
							break;
						}
					}
				}
				if (isErrAndOr) {
					errorMessage = R.getResource("catalog.promotion.import.andor.not.match.in.level", promotionCode);
				}
			}
		}

		return errorMessage;
	}

	private String exportFail() throws Exception {
		SXSSFWorkbook workbook = null;
		OutputStream out = null;
		try {
			if ((listUnitError == null || listUnitError.isEmpty()) && (lstHeaderError == null || lstHeaderError.isEmpty()) && (lstDetailError == null || lstDetailError.isEmpty()) && (lstPromotionShopError == null || lstPromotionShopError.isEmpty())) {
				return "";
			}
			numFail = 0;
			numFail = lstHeaderError != null ? lstHeaderError.size() : 0;
			numFail = numFail + (lstDetailError != null ? lstDetailError.size() : 0);
			numFail = numFail + (lstPromotionShopError != null ? lstPromotionShopError.size() : 0);

			//			if (lstHeaderError != null && lstHeaderError.size() > 0) {
			//				numFail = lstHeaderError.size();
			//			}
			//			if (lstDetailError != null && lstDetailError.size() > 0) {
			//				numFail = numFail + lstDetailError.size();
			//			}
			//			if (lstPromotionShopError != null && lstPromotionShopError.size() > 0) {
			//				numFail = numFail + lstPromotionShopError.size();
			//			}

			String outputName = "Bieu_mau_thong_tin_CTKM" + DateUtil.toDateString(DateUtil.now(), DateUtil.DATE_FORMAT_EXCEL_FILE) + FileExtension.XLSX.getValue();
			workbook = new SXSSFWorkbook(200);
			workbook.setCompressTempFiles(true);

			String stmp = R.getResource("catalog.promotion.import.sheetNames");
			String[] str = null;
			if (stmp != null) {
				str = stmp.split(";");
			} else {
				str = new String[] { "1", "2", "3" };
			}
			SXSSFSheet sheetHeader = (SXSSFSheet) workbook.createSheet(str.length > 0 ? str[0] : "1");
			Map<String, XSSFCellStyle> style = ExcelPOIProcessUtils.createStyles(workbook);
			sheetHeader.setDefaultRowHeight((short) (15 * 20));
			sheetHeader.setDefaultColumnWidth(13);
			XSSFCellStyle headerStyle = (XSSFCellStyle) style.get(ExcelPOIProcessUtils.MENU).clone();
			ExcelPOIProcessUtils.setBorderForCell(headerStyle, BorderStyle.THIN, ExcelPOIProcessUtils.poiBlack);
			XSSFCellStyle leftStyle = style.get(ExcelPOIProcessUtils.ROW_LEFT);
			XSSFCellStyle rightStyle = style.get(ExcelPOIProcessUtils.ROW_RIGHT);
			XSSFFont rFont = (XSSFFont) workbook.createFont();
			ExcelPOIProcessUtils.setFontPOI(rFont, ExcelPOIProcessUtils.ARIAL_FONT_NAME, 9, false, ExcelPOIProcessUtils.poiRed);
			XSSFCellStyle errStyle = (XSSFCellStyle) leftStyle.clone();
			errStyle.setFont(rFont);
			errStyle.setVerticalAlignment(XSSFCellStyle.VERTICAL_TOP);
			errStyle.setWrapText(true);
			style = null;
			SXSSFReportHelper.addCell(sheetHeader, 0, 0, "Mã CTKM", headerStyle);
			SXSSFReportHelper.addCell(sheetHeader, 1, 0, "Tên CTKM", headerStyle);
			SXSSFReportHelper.addCell(sheetHeader, 2, 0, "Phiên Bản", headerStyle);
			SXSSFReportHelper.addCell(sheetHeader, 3, 0, "Loại CTKM", headerStyle);
			SXSSFReportHelper.addCell(sheetHeader, 4, 0, "Từ Ngày", headerStyle);
			SXSSFReportHelper.addCell(sheetHeader, 5, 0, "Đến Ngày", headerStyle);
			
			SXSSFReportHelper.addCell(sheetHeader, 6, 0, "Số thông báo CTKM", headerStyle);
			SXSSFReportHelper.addCell(sheetHeader, 7, 0, "Nhóm/Tên SP hàng bán", headerStyle);
			SXSSFReportHelper.addCell(sheetHeader, 8, 0, "Mô tả chương trình", headerStyle);
			SXSSFReportHelper.addCell(sheetHeader, 9, 0, "Bội số", headerStyle);
			SXSSFReportHelper.addCell(sheetHeader, 10, 0, "Tối ưu", headerStyle);
     		SXSSFReportHelper.addCell(sheetHeader, 11, 0, "Loại trả thưởng", headerStyle);
			SXSSFReportHelper.addCell(sheetHeader, 12, 0, "Từ ngày trả thưởng", headerStyle);
			SXSSFReportHelper.addCell(sheetHeader, 13, 0, "Đến ngày trả thưởng", headerStyle);
			
			SXSSFReportHelper.addCell(sheetHeader, 14, 0, "Mô tả lỗi", headerStyle);
			if (lstHeaderError != null) {
				for (int i = 0; i < lstHeaderError.size(); i++) {
					SXSSFReportHelper.addCell(sheetHeader, 0, i + 1, lstHeaderError.get(i).getContent1(), leftStyle);
					SXSSFReportHelper.addCell(sheetHeader, 1, i + 1, lstHeaderError.get(i).getContent2(), leftStyle);
					SXSSFReportHelper.addCell(sheetHeader, 2, i + 1, lstHeaderError.get(i).getContent3(), rightStyle);
					SXSSFReportHelper.addCell(sheetHeader, 3, i + 1, lstHeaderError.get(i).getContent4(), leftStyle);
					SXSSFReportHelper.addCell(sheetHeader, 4, i + 1, lstHeaderError.get(i).getContent5(), leftStyle);
					SXSSFReportHelper.addCell(sheetHeader, 5, i + 1, lstHeaderError.get(i).getContent6(), leftStyle);
					
					SXSSFReportHelper.addCell(sheetHeader, 6, i + 1, lstHeaderError.get(i).getContent7(), leftStyle);
					SXSSFReportHelper.addCell(sheetHeader, 7, i + 1, lstHeaderError.get(i).getContent8(), leftStyle);
					SXSSFReportHelper.addCell(sheetHeader, 8, i + 1, lstHeaderError.get(i).getContent9(), leftStyle);
					SXSSFReportHelper.addCell(sheetHeader, 9, i + 1, lstHeaderError.get(i).getContent10(), leftStyle);
					SXSSFReportHelper.addCell(sheetHeader, 10, i + 1, lstHeaderError.get(i).getContent11(), leftStyle);
					SXSSFReportHelper.addCell(sheetHeader, 11, i + 1, lstHeaderError.get(i).getContent12(), leftStyle);
					SXSSFReportHelper.addCell(sheetHeader, 12, i + 1, lstHeaderError.get(i).getContent13(), leftStyle);
					SXSSFReportHelper.addCell(sheetHeader, 13, i + 1, lstHeaderError.get(i).getContent14(), leftStyle);
					
					SXSSFReportHelper.addCell(sheetHeader, 14, i + 1, lstHeaderError.get(i).getErrMsg(), errStyle);
				}
			}
			SXSSFSheet sheetDetail = (SXSSFSheet) workbook.createSheet(str.length > 1 ? str[1] : "2");
			sheetDetail.setDefaultRowHeight((short) (15 * 20));
			sheetDetail.setDefaultColumnWidth(13);
			SXSSFReportHelper.addCell(sheetDetail, 0, 0, "Mã CTKM", headerStyle);
			SXSSFReportHelper.addCell(sheetDetail, 1, 0, "Loại CTKM", headerStyle);
			
/*			SXSSFReportHelper.addCell(sheetDetail, 2, 0, "Mã nhóm", headerStyle);
			SXSSFReportHelper.addCell(sheetDetail, 3, 0, "Tên nhóm", headerStyle);
			SXSSFReportHelper.addCell(sheetDetail, 4, 0, "Mã mức", headerStyle);*/
			
			SXSSFReportHelper.addCell(sheetDetail, 2, 0, "Mã Sản Phẩm Mua", headerStyle);
			SXSSFReportHelper.addCell(sheetDetail, 3, 0, "SL Sản Phẩm Mua", headerStyle);
			SXSSFReportHelper.addCell(sheetDetail, 4, 0, "Đơn Vị Tính Cho SP Mua", headerStyle);
			SXSSFReportHelper.addCell(sheetDetail, 5, 0, "Số Tiền SP Mua", headerStyle);
			SXSSFReportHelper.addCell(sheetDetail, 6, 0, "Số Tiền SP KM", headerStyle);
			SXSSFReportHelper.addCell(sheetDetail, 7, 0, "% KM", headerStyle);
			SXSSFReportHelper.addCell(sheetDetail, 8, 0, "Mã SP KM", headerStyle);
			SXSSFReportHelper.addCell(sheetDetail, 9, 0, "Số Lượng KM", headerStyle);
			SXSSFReportHelper.addCell(sheetDetail, 10, 0, "Đơn Vị Tính cho SP KM", headerStyle);
			SXSSFReportHelper.addCell(sheetDetail, 11, 0, "Thuộc tính bắt buộc", headerStyle);
			
/*			SXSSFReportHelper.addCell(sheetDetail, 15, 0, "Bội số", headerStyle);
			SXSSFReportHelper.addCell(sheetDetail, 16, 0, "Tối ưu", headerStyle);
			SXSSFReportHelper.addCell(sheetDetail, 17, 0, "ĐKGH", headerStyle);*/
			
			SXSSFReportHelper.addCell(sheetDetail, 12, 0, "Mô tả lỗi", headerStyle);
			if (lstDetailError != null) {
				for (int i = 0; i < lstDetailError.size(); i++) {
					SXSSFReportHelper.addCell(sheetDetail, 0, i + 1, lstDetailError.get(i).getContent1(), leftStyle);
					SXSSFReportHelper.addCell(sheetDetail, 1, i + 1, lstDetailError.get(i).getContent2(), leftStyle);
					
/*					SXSSFReportHelper.addCell(sheetDetail, 2, i + 1, lstDetailError.get(i).getContent3(), leftStyle);
					SXSSFReportHelper.addCell(sheetDetail, 3, i + 1, lstDetailError.get(i).getContent4(), leftStyle);
					SXSSFReportHelper.addCell(sheetDetail, 4, i + 1, lstDetailError.get(i).getContent5(), leftStyle);*/
					
					SXSSFReportHelper.addCell(sheetDetail, 2, i + 1, lstDetailError.get(i).getContent3(), leftStyle);
					SXSSFReportHelper.addCell(sheetDetail, 3, i + 1, lstDetailError.get(i).getContent4(), rightStyle);
					SXSSFReportHelper.addCell(sheetDetail, 4, i + 1, lstDetailError.get(i).getContent5(), leftStyle);
					SXSSFReportHelper.addCell(sheetDetail, 5, i + 1, lstDetailError.get(i).getContent6(), rightStyle);
					SXSSFReportHelper.addCell(sheetDetail, 6, i + 1, lstDetailError.get(i).getContent7(), rightStyle);
					SXSSFReportHelper.addCell(sheetDetail, 7, i + 1, lstDetailError.get(i).getContent8(), rightStyle);
					SXSSFReportHelper.addCell(sheetDetail, 8, i + 1, lstDetailError.get(i).getContent9(), leftStyle);
					SXSSFReportHelper.addCell(sheetDetail, 9, i + 1, lstDetailError.get(i).getContent10(), rightStyle);
					SXSSFReportHelper.addCell(sheetDetail, 10, i + 1, lstDetailError.get(i).getContent11(), leftStyle);
					SXSSFReportHelper.addCell(sheetDetail, 11, i + 1, lstDetailError.get(i).getContent12(), rightStyle);
					
					
					/*SXSSFReportHelper.addCell(sheetDetail, 15, i + 1, lstDetailError.get(i).getContent16(), rightStyle);
					SXSSFReportHelper.addCell(sheetDetail, 16, i + 1, lstDetailError.get(i).getContent17(), rightStyle);
					SXSSFReportHelper.addCell(sheetDetail, 17, i + 1, lstDetailError.get(i).getContent18(), rightStyle);*/
					
					SXSSFReportHelper.addCell(sheetDetail, 12, i + 1, lstDetailError.get(i).getContent13(), errStyle);

				}
			}
			SXSSFSheet sheetUnit = (SXSSFSheet) workbook.createSheet(str.length > 2 ? str[2] : "3");
			sheetUnit.setDefaultRowHeight((short) (15 * 20));
			sheetUnit.setDefaultColumnWidth(13);
			SXSSFReportHelper.addCell(sheetUnit, 0, 0, "Mã CTKM", headerStyle);
			SXSSFReportHelper.addCell(sheetUnit, 1, 0, "Mã đơn vị", headerStyle);
			SXSSFReportHelper.addCell(sheetUnit, 2, 0, "Số suất", headerStyle);
			SXSSFReportHelper.addCell(sheetUnit, 3, 0, "Số tiền", headerStyle);
			SXSSFReportHelper.addCell(sheetUnit, 4, 0, "Số lượng", headerStyle);
			SXSSFReportHelper.addCell(sheetUnit, 5, 0, "Mô tả lỗi", headerStyle);
			/*if (lstDetailError != null) {
				for (int i = 0; i < lstDetailError.size(); i++) {*/
			if(listUnitError != null){
				for(int i = 0; i< listUnitError.size(); i++){
					SXSSFReportHelper.addCell(sheetUnit, 0, i + 1, listUnitError.get(i).getContent1(), leftStyle);
					SXSSFReportHelper.addCell(sheetUnit, 1, i + 1, listUnitError.get(i).getContent2(), leftStyle);
					SXSSFReportHelper.addCell(sheetUnit, 2, i + 1, listUnitError.get(i).getContent3(), leftStyle);
					SXSSFReportHelper.addCell(sheetUnit, 3, i + 1, listUnitError.get(i).getContent4(), leftStyle);
					SXSSFReportHelper.addCell(sheetUnit, 4, i + 1, listUnitError.get(i).getContent5(), leftStyle);
					SXSSFReportHelper.addCell(sheetUnit, 5, i + 1, listUnitError.get(i).getContent6(), errStyle);
				}
			}			
			String exportFileName = Configuration.getStoreImportDownloadPath() + outputName;
			out = new FileOutputStream(exportFileName);
			workbook.write(out);
			return Configuration.getStoreImportFailDownloadPath() + outputName;
		} catch (Exception e) {
			LogUtility.logErrorStandard(e, R.getResource("web.log.message.error", "vnm.web.action.program.PromotionCatalogAction.exportFail"), createLogErrorStandard(actionStartTime));
			errMsg = ValidateUtil.getErrorMsg(ConstantManager.ERR_SYSTEM);
			result.put("errMsg", errMsg);
		} finally {
			if (out != null) {
				IOUtils.closeQuietly(out);
			}
			if (workbook != null) {
				workbook.dispose();
			}
		}
		return null;
	}

	public String validateLineQuantity(String promotionCode, ListGroupMua listGroupMua) {
		String errorMessage = "";
		for (int i = 0; i < listGroupMua.size(); i++) {
			GroupMua groupMua = listGroupMua.get(i);
			if (groupMua.lstLevel.size() == 1) {
				if (groupMua.lstLevel.get(0).lstSP.size() != 1) {
					errorMessage = Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "catalog.promotion.import.type.zv01.product.only.one", promotionCode);
					return errorMessage;
				}
				if (StringUtil.isNullOrEmpty(groupMua.lstLevel.get(0).lstSP.get(0).productCode) || groupMua.lstLevel.get(0).lstSP.get(0).quantity == null) {
					errorMessage = Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "catalog.promotion.import.type.zv09.no.product.or.no.quantity", promotionCode);
					return errorMessage;
				}
			} else {
				GroupSP lastLevel = groupMua.lstLevel.get(0);
				if (lastLevel.lstSP.size() != 1) {
					errorMessage = Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "catalog.promotion.import.type.zv01.product.only.one", promotionCode);
					return errorMessage;
				}
				if (StringUtil.isNullOrEmpty(lastLevel.lstSP.get(0).productCode) || lastLevel.lstSP.get(0).quantity == null) {
					errorMessage = Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "catalog.promotion.import.type.zv09.no.product.or.no.quantity", promotionCode);
					return errorMessage;
				}
				for (int j = 1; j < groupMua.lstLevel.size(); j++) {
					if (groupMua.lstLevel.get(j).lstSP.size() != 1) {
						errorMessage = Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "catalog.promotion.import.type.zv01.product.only.one", promotionCode);
						return errorMessage;
					}
					if (StringUtil.isNullOrEmpty(groupMua.lstLevel.get(j).lstSP.get(0).productCode) || groupMua.lstLevel.get(j).lstSP.get(0).quantity == null) {
						errorMessage = Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "catalog.promotion.import.type.zv09.no.product.or.no.quantity", promotionCode);
						return errorMessage;
					}
					if (!lastLevel.lstSP.get(0).productCode.equals(groupMua.lstLevel.get(j).lstSP.get(0).productCode)) {
						errorMessage = Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "catalog.promotion.import.type.zv01.product.not.same", promotionCode);
						return errorMessage;
					}
					if (lastLevel.lstSP.get(0).quantity.compareTo(groupMua.lstLevel.get(j).lstSP.get(0).quantity) == 1) {
						errorMessage = Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "catalog.promotion.import.type.zv01.quantity.continue", promotionCode);
						return errorMessage;
					}
					lastLevel = groupMua.lstLevel.get(j);
				}
			}
		}
		return errorMessage;
	}

	public String validateLineQuantityUnit(String promotionCode, ListGroupMua listGroupMua) {
		String errorMessage = "";
		for (int i = 0; i < listGroupMua.size(); i++) {
			GroupMua groupMua = listGroupMua.get(i);
			if (groupMua.lstLevel.size() == 1) {
				if (groupMua.lstLevel.get(0).lstSP.size() != 1) {
					errorMessage = Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "catalog.promotion.import.type.zv01.product.only.one", promotionCode);
					return errorMessage;
				}
				if (StringUtil.isNullOrEmpty(groupMua.lstLevel.get(0).lstSP.get(0).productCode) || groupMua.lstLevel.get(0).lstSP.get(0).quantityUnit == null) {
					errorMessage = Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "catalog.promotion.import.type.zv09.no.product.or.no.quantity.unit", promotionCode);
					return errorMessage;
				}
			} else {
				GroupSP lastLevel = groupMua.lstLevel.get(0);
				if (lastLevel.lstSP.size() != 1) {
					errorMessage = Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "catalog.promotion.import.type.zv01.product.only.one", promotionCode);
					return errorMessage;
				}
				if (StringUtil.isNullOrEmpty(lastLevel.lstSP.get(0).productCode) || lastLevel.lstSP.get(0).quantityUnit == null) {
					errorMessage = Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "catalog.promotion.import.type.zv09.no.product.or.no.quantity.unit", promotionCode);
					return errorMessage;
				}
				for (int j = 1; j < groupMua.lstLevel.size(); j++) {
					if (!lastLevel.lstSP.get(0).quantityUnit.equals(groupMua.lstLevel.get(j).lstSP.get(0).quantityUnit)) {
						errorMessage = Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "catalog.promotion.import.quantity.unit.not.same", promotionCode);
						return errorMessage;
					}
					lastLevel = groupMua.lstLevel.get(j);
				}
			}
		}
		return errorMessage;
	}

	public String validateLineAmount(String promotionCode, ListGroupMua listGroupMua) {
		String errorMessage = "";
		for (int i = 0; i < listGroupMua.size(); i++) {
			GroupMua groupMua = listGroupMua.get(i);
			if (groupMua.lstLevel.size() == 1) {
				if (groupMua.lstLevel.get(0).lstSP.size() != 1) {
					errorMessage = Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "catalog.promotion.import.type.zv01.product.only.one", promotionCode);
					return errorMessage;
				}
				if (StringUtil.isNullOrEmpty(groupMua.lstLevel.get(0).lstSP.get(0).productCode) && groupMua.lstLevel.get(0).lstSP.get(0).amount == null) {
					errorMessage = Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "catalog.promotion.import.type.zv09.no.product.or.no.quantity", promotionCode);
					return errorMessage;
				}
			} else {
				GroupSP lastLevel = groupMua.lstLevel.get(0);
				if (lastLevel.lstSP.size() != 1) {
					errorMessage = Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "catalog.promotion.import.type.zv01.product.only.one", promotionCode);
					return errorMessage;
				}
				if (StringUtil.isNullOrEmpty(lastLevel.lstSP.get(0).productCode) || lastLevel.lstSP.get(0).amount == null) {
					errorMessage = Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "catalog.promotion.import.type.zv09.no.product.or.no.amount", promotionCode);
					return errorMessage;
				}
				for (int j = 0; j < groupMua.lstLevel.size(); j++) {
					if (groupMua.lstLevel.get(j).lstSP.size() != 1) {
						errorMessage = Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "catalog.promotion.import.type.zv01.product.only.one", promotionCode);
						return errorMessage;
					}
					if (StringUtil.isNullOrEmpty(groupMua.lstLevel.get(j).lstSP.get(0).productCode) && groupMua.lstLevel.get(j).lstSP.get(0).amount == null) {
						errorMessage = Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "catalog.promotion.import.type.zv09.no.product.or.no.quantity", promotionCode);
						return errorMessage;
					}
					if (!lastLevel.lstSP.get(0).productCode.equals(groupMua.lstLevel.get(j).lstSP.get(0).productCode)) {
						errorMessage = Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "catalog.promotion.import.type.zv01.product.not.same", promotionCode);
						return errorMessage;
					}
					if (lastLevel.lstSP.get(0).amount.compareTo(groupMua.lstLevel.get(j).lstSP.get(0).amount) > 0) {
						errorMessage = Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "catalog.promotion.import.type.zv01.amount.continue", promotionCode);
						return errorMessage;
					}
					lastLevel = groupMua.lstLevel.get(j);
				}
			}
		}
		return errorMessage;
	}

	public String validateGroupQuantity(String promotionCode, ListGroupMua listGroupMua) {
		String errorMessage = "";
		for (int i = 0; i < listGroupMua.size(); i++) {
			GroupMua groupMua = listGroupMua.get(i);
			if (groupMua.lstLevel.size() == 1) {
				for (int ii = 0; ii < groupMua.lstLevel.get(0).lstSP.size(); ii++) {
					if (StringUtil.isNullOrEmpty(groupMua.lstLevel.get(0).lstSP.get(ii).productCode) || groupMua.lstLevel.get(0).lstSP.get(ii).quantity == null) {
						errorMessage = Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "catalog.promotion.import.type.zv09.no.product.or.no.quantity", promotionCode);
						return errorMessage;
					}
					if (ii != 0) {
						if (groupMua.lstLevel.get(0).lstSP.get(ii).quantity != null && groupMua.lstLevel.get(0).lstSP.get(ii - 1).quantity != null && !groupMua.lstLevel.get(0).lstSP.get(ii).quantity.equals(groupMua.lstLevel.get(0).lstSP.get(ii
								- 1).quantity)) {
							errorMessage = Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "catalog.promotion.import.quantity.not.same", promotionCode);
							return errorMessage;
						}
					}
				}
			} else {
				for (int j = 1; j < groupMua.lstLevel.size(); j++) {
					GroupSP preLevel = groupMua.lstLevel.get(j - 1);
					GroupSP level = groupMua.lstLevel.get(j);
					BigDecimal preQuantity;
					BigDecimal quantity;
					if (preLevel.lstSP.size() != level.lstSP.size()) {
						errorMessage = Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "catalog.promotion.import.type.zv09.product.not.same", promotionCode);
						return errorMessage;
					} else {
						for (int ii = 0; ii < level.lstSP.size(); ii++) {
							if (StringUtil.isNullOrEmpty(level.lstSP.get(ii).productCode) || level.lstSP.get(ii).quantity == null) {
								errorMessage = Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "catalog.promotion.import.type.zv09.no.product.or.no.quantity", promotionCode);
								return errorMessage;
							}
							if (!checkMatch2Level(preLevel, level.lstSP.get(ii).productCode)) {
								errorMessage = Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "catalog.promotion.import.type.zv09.product.not.same", promotionCode);
								return errorMessage;
							}
							if (ii != 0) {
								if (level.lstSP.get(ii).quantity != null && level.lstSP.get(ii - 1).quantity != null && !level.lstSP.get(ii).quantity.equals(level.lstSP.get(ii - 1).quantity)) {
									errorMessage = Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "catalog.promotion.import.quantity.not.same", promotionCode);
									return errorMessage;
								}
							}
						}
					}
					preQuantity = preLevel.lstSP.get(0).quantity;
					quantity = level.lstSP.get(0).quantity;
					if (preQuantity.compareTo(quantity) >= 0) {
						errorMessage = Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "catalog.promotion.import.type.zv01.quantity.continue", promotionCode);
						return errorMessage;
					}
				}
			}
		}
		return errorMessage;
	}

	public String validateGroupAmount(String promotionCode, ListGroupMua listGroupMua) {
		String errorMessage = "";
		for (int i = 0; i < listGroupMua.size(); i++) {
			GroupMua groupMua = listGroupMua.get(i);
			if (groupMua.lstLevel.size() == 1) {
				for (int ii = 0; ii < groupMua.lstLevel.get(0).lstSP.size(); ii++) {
					if (StringUtil.isNullOrEmpty(groupMua.lstLevel.get(0).lstSP.get(ii).productCode) || groupMua.lstLevel.get(0).lstSP.get(ii).amount == null) {
						errorMessage = Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "catalog.promotion.import.type.zv09.no.product.or.no.quantity", promotionCode);
						return errorMessage;
					}
					if (ii != 0) {
						if (groupMua.lstLevel.get(0).lstSP.get(ii).amount != null && groupMua.lstLevel.get(0).lstSP.get(ii - 1).amount != null && !groupMua.lstLevel.get(0).lstSP.get(ii).amount.equals(groupMua.lstLevel.get(0).lstSP.get(ii
								- 1).amount)) {
							errorMessage = Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "catalog.promotion.import.type.zv09.sale.amount.not.same", promotionCode);
							return errorMessage;
						}
					}
				}
			} else {
				for (int j = 1; j < groupMua.lstLevel.size(); j++) {
					GroupSP preLevel = groupMua.lstLevel.get(j - 1);
					GroupSP level = groupMua.lstLevel.get(j);
					BigDecimal preAmount;
					BigDecimal amount;
					if (preLevel.lstSP.size() != level.lstSP.size()) {
						errorMessage = Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "catalog.promotion.import.type.zv09.product.not.same", promotionCode);
						return errorMessage;
					} else {
						for (int ii = 0; ii < level.lstSP.size(); ii++) {
							if (StringUtil.isNullOrEmpty(level.lstSP.get(ii).productCode) || level.lstSP.get(ii).amount == null) {
								errorMessage = Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "catalog.promotion.import.type.zv09.no.product.or.no.quantity", promotionCode);
								return errorMessage;
							}
							if (!checkMatch2Level(preLevel, level.lstSP.get(ii).productCode)) {
								errorMessage = Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "catalog.promotion.import.type.zv09.product.not.same", promotionCode);
								return errorMessage;
							}
							if (ii != 0) {
								if (level.lstSP.get(ii).amount != null && level.lstSP.get(ii - 1).amount != null && !level.lstSP.get(ii).amount.equals(level.lstSP.get(ii - 1).amount)) {
									errorMessage = Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "catalog.promotion.import.amount.not.same", promotionCode);
									return errorMessage;
								}
							}
						}
					}
					preAmount = preLevel.lstSP.get(0).amount;
					amount = level.lstSP.get(0).amount;
					if (preAmount.compareTo(amount) > 0) {
						errorMessage = Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "catalog.promotion.import.type.zv01.amount.continue", promotionCode);
						return errorMessage;
					}
				}
			}
		}
		return errorMessage;
	}

	public String validateGroupQuantityUnit(String promotionCode, ListGroupMua listGroupMua) {
		String errorMessage = "";
		for (int i = 0; i < listGroupMua.size(); i++) {
			GroupMua groupMua = listGroupMua.get(i);
			if (groupMua.lstLevel.size() == 1) {
				for (int ii = 0; ii < groupMua.lstLevel.get(0).lstSP.size(); ii++) {
					if (StringUtil.isNullOrEmpty(groupMua.lstLevel.get(0).lstSP.get(ii).productCode) || groupMua.lstLevel.get(0).lstSP.get(ii).quantity == null) {
						errorMessage = Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "catalog.promotion.import.type.zv09.no.product.or.no.quantity", promotionCode);
						return errorMessage;
					}
					if (ii != 0) {
						if (groupMua.lstLevel.get(0).lstSP.get(ii).quantityUnit != null && groupMua.lstLevel.get(0).lstSP.get(ii - 1).quantityUnit != null && !groupMua.lstLevel.get(0).lstSP.get(ii).quantityUnit.equals(groupMua.lstLevel.get(0).lstSP
								.get(ii - 1).quantityUnit)) {
							errorMessage = Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "catalog.promotion.import.quantity.unit.not.same", promotionCode);
							return errorMessage;
						}
					}
				}
			} else {
				for (int j = 1; j < groupMua.lstLevel.size(); j++) {
					GroupSP preLevel = groupMua.lstLevel.get(j - 1);
					GroupSP level = groupMua.lstLevel.get(j);
					if (preLevel.lstSP.size() != level.lstSP.size()) {
						errorMessage = Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "catalog.promotion.import.type.zv09.product.not.same", promotionCode);
						return errorMessage;
					} else {
						for (int ii = 0; ii < level.lstSP.size(); ii++) {
							if (StringUtil.isNullOrEmpty(level.lstSP.get(ii).productCode) || level.lstSP.get(ii).quantity == null) {
								errorMessage = Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "catalog.promotion.import.type.zv09.no.product.or.no.quantity", promotionCode);
								return errorMessage;
							}
							if (!checkMatch2Level(preLevel, level.lstSP.get(ii).productCode)) {
								errorMessage = Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "catalog.promotion.import.type.zv09.product.not.same", promotionCode);
								return errorMessage;
							}
							if (ii != 0) {
								if (level.lstSP.get(ii).quantityUnit != null && level.lstSP.get(ii - 1).quantityUnit != null && !level.lstSP.get(ii).quantityUnit.equals(level.lstSP.get(ii - 1).quantityUnit)) {
									errorMessage = Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "catalog.promotion.import.quantity.unit.not.same", promotionCode);
									return errorMessage;
								}
							}
						}
					}
				}
			}
		}
		return errorMessage;
	}

	public String validateBundleQuantity(String promotionCode, ListGroupMua listGroupMua) {
		String errorMessage = "";
		for (int i = 0; i < listGroupMua.size(); i++) {
			GroupMua groupMua = listGroupMua.get(i);
			if (groupMua.lstLevel.size() == 1) {
				for (int j = 0; j < groupMua.lstLevel.get(0).lstSP.size(); j++) {
					if (StringUtil.isNullOrEmpty(groupMua.lstLevel.get(0).lstSP.get(j).productCode) || groupMua.lstLevel.get(0).lstSP.get(j).quantity == null) {
						errorMessage = Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "catalog.promotion.import.type.zv14.product.quantity", promotionCode);
						return errorMessage;
					}
				}
			} else {
				for (int j = 1; j < groupMua.lstLevel.size(); j++) {
					GroupSP preLevel = groupMua.lstLevel.get(j - 1);
					GroupSP level = groupMua.lstLevel.get(j);
					if (preLevel.lstSP.size() != level.lstSP.size()) {
						errorMessage = Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "catalog.promotion.import.type.zv09.product.not.same", promotionCode);
					} else {
						for (int ii = 0; ii < level.lstSP.size(); ii++) {
							if (StringUtil.isNullOrEmpty(level.lstSP.get(ii).productCode) || level.lstSP.get(ii).quantity == null) {
								errorMessage = Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "catalog.promotion.import.type.zv09.no.product.or.no.quantity", promotionCode);
								return errorMessage;
							}
							if (!checkMatch2Level(preLevel, level.lstSP.get(ii).productCode)) {
								errorMessage = Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "catalog.promotion.import.type.zv09.product.not.same", promotionCode);
								return errorMessage;
							}
						}
					}
				}
			}
		}
		return errorMessage;
	}

	public String validateBundleQuantityUnit(String promotionCode, ListGroupMua listGroupMua) {
		String errorMessage = "";
		for (int i = 0; i < listGroupMua.size(); i++) {
			GroupMua groupMua = listGroupMua.get(i);
			if (groupMua.lstLevel.size() == 1) {
				GroupSP level = groupMua.lstLevel.get(0);
				for (int j = 0; j < level.lstSP.size(); j++) {
					if (StringUtil.isNullOrEmpty(groupMua.lstLevel.get(0).lstSP.get(j).productCode) || groupMua.lstLevel.get(0).lstSP.get(j).quantityUnit == null) {
						errorMessage = Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "catalog.promotion.import.type.zv14.product.quantity", promotionCode);
						return errorMessage;
					}
					if (j != 0) {
						if (level.lstSP.get(j).quantityUnit != null && level.lstSP.get(j - 1).quantityUnit != null && !level.lstSP.get(j).quantityUnit.equals(level.lstSP.get(j - 1).quantityUnit)) {
							errorMessage = Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "catalog.promotion.import.quantity.unit.not.same", promotionCode);
							return errorMessage;
						}
					}
				}
			} else {
				for (int j = 1; j < groupMua.lstLevel.size(); j++) {
					GroupSP preLevel = groupMua.lstLevel.get(j - 1);
					Integer unit = 0;
					if (preLevel.lstSP.size() > 0) {
						for (int k = 0; k < preLevel.lstSP.size(); k++) {
							if (k == 0) {
								unit = preLevel.lstSP.get(k).quantityUnit;
							}
							if (k > 0 && !unit.equals(preLevel.lstSP.get(k).quantityUnit)) {
								errorMessage = Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "catalog.promotion.import.quantity.unit.not.same", promotionCode);
								return errorMessage;
							}
						}
					}
					GroupSP level = groupMua.lstLevel.get(j);
					if (preLevel.lstSP.size() != level.lstSP.size()) {
						errorMessage = Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "catalog.promotion.import.type.zv09.product.not.same", promotionCode);
					} else {
						for (int ii = 0; ii < level.lstSP.size(); ii++) {
							if (ii == 0 && unit > 0 && !unit.equals(level.lstSP.get(ii).quantityUnit)) {
								errorMessage = Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "catalog.promotion.import.quantity.unit.not.same", promotionCode);
								return errorMessage;
							}
							if (StringUtil.isNullOrEmpty(level.lstSP.get(ii).productCode) || level.lstSP.get(ii).quantityUnit == null) {
								errorMessage = Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "catalog.promotion.import.type.zv09.no.product.or.no.quantity", promotionCode);
								return errorMessage;
							}

							if (!checkMatch2Level(preLevel, level.lstSP.get(ii).productCode)) {
								errorMessage = Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "catalog.promotion.import.type.zv09.product.not.same", promotionCode);
								return errorMessage;
							}
							if (ii != 0) {
								if (level.lstSP.get(ii).quantityUnit != null && level.lstSP.get(ii - 1).quantityUnit != null && !level.lstSP.get(ii).quantityUnit.equals(level.lstSP.get(ii - 1).quantityUnit)) {
									errorMessage = Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "catalog.promotion.import.quantity.unit.not.same", promotionCode);
									return errorMessage;
								}
							}
						}
					}
				}
			}
		}
		return errorMessage;
	}

	public String validateBundleAmount(String promotionCode, ListGroupMua listGroupMua) {
		String errorMessage = "";
		for (int i = 0; i < listGroupMua.size(); i++) {
			GroupMua groupMua = listGroupMua.get(i);
			if (groupMua.lstLevel.size() == 1) {
				for (int j = 0; j < groupMua.lstLevel.get(0).lstSP.size(); j++) {
					if (StringUtil.isNullOrEmpty(groupMua.lstLevel.get(0).lstSP.get(j).productCode) || groupMua.lstLevel.get(0).lstSP.get(j).amount == null) {
						errorMessage = Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "catalog.promotion.import.type.zv14.product.amount", promotionCode);
						return errorMessage;
					}
				}
			} else {
				for (int j = 1; j < groupMua.lstLevel.size(); j++) {
					GroupSP preLevel = groupMua.lstLevel.get(j - 1);
					GroupSP level = groupMua.lstLevel.get(j);
					if (preLevel.lstSP.size() != level.lstSP.size()) {
						errorMessage = Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "catalog.promotion.import.type.zv09.product.not.same", promotionCode);
					} else {
						for (int ii = 0; ii < level.lstSP.size(); ii++) {
							if (StringUtil.isNullOrEmpty(level.lstSP.get(ii).productCode) || level.lstSP.get(ii).amount == null) {
								errorMessage = Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "catalog.promotion.import.type.zv09.no.product.or.no.quantity", promotionCode);
								return errorMessage;
							}
							if (!checkMatch2Level(preLevel, level.lstSP.get(ii).productCode)) {
								errorMessage = Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "catalog.promotion.import.type.zv09.product.not.same", promotionCode);
								return errorMessage;
							}
						}
					}
				}
			}
		}
		return errorMessage;
	}

	public String validateAmount(String promotionCode, ListGroupMua listGroupMua) {
		String errorMessage = "";
		for (int i = 0; i < listGroupMua.size(); i++) {
			GroupMua groupMua = listGroupMua.get(i);
			if (groupMua.lstLevel.size() == 1) {
				if (groupMua.lstLevel.get(0).lstSP.get(0).amount == null) {
					errorMessage = Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "catalog.promotion.import.type.zv19.amount", promotionCode);
					return errorMessage;
				}
			} else {
				GroupSP lastLevel = groupMua.lstLevel.get(0);
				if (lastLevel.lstSP.get(0).amount == null) {
					errorMessage = Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "catalog.promotion.import.type.zv19.amount", promotionCode);
					return errorMessage;
				}
				for (int j = 1; j < groupMua.lstLevel.size(); j++) {
					if (groupMua.lstLevel.get(j).lstSP.get(0).amount == null) {
						errorMessage = Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "catalog.promotion.import.type.zv19.amount", promotionCode);
						return errorMessage;
					}
					if (groupMua.lstLevel.get(j).lstSP.get(0).amount.equals(lastLevel.lstSP.get(0).amount)) {
						errorMessage = Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "catalog.promotion.import.type.zv19.trung.du.lien", promotionCode);
						return errorMessage;
					}
					lastLevel = groupMua.lstLevel.get(j);
				}
			}
		}
		return errorMessage;
	}

	public String validateQuantity(String promotionCode, ListGroupMua listGroupMua) {
		String errorMessage = "";
		for (int i = 0; i < listGroupMua.size(); i++) {
			GroupMua groupMua = listGroupMua.get(i);
			if (groupMua.lstLevel.size() == 1) {
				if (groupMua.lstLevel.get(0).lstSP.get(0).quantity == null) {
					errorMessage = Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "catalog.promotion.import.type.zv22.quantity", promotionCode);
					return errorMessage;
				}
			} else {
				GroupSP lastLevel = groupMua.lstLevel.get(0);
				if (lastLevel.lstSP.get(0).quantity == null) {
					errorMessage = Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "catalog.promotion.import.type.zv22.quantity", promotionCode);
					return errorMessage;
				}
				for (int j = 1; j < groupMua.lstLevel.size(); j++) {
					if (groupMua.lstLevel.get(j).lstSP.get(0).quantity == null) {
						errorMessage = Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "catalog.promotion.import.type.zv22.quantity", promotionCode);
						return errorMessage;
					}
					if (groupMua.lstLevel.get(j).lstSP.get(0).quantity.equals(lastLevel.lstSP.get(0).quantity)) {
						errorMessage = Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "catalog.promotion.import.type.zv22.trung.du.lien", promotionCode);
						return errorMessage;
					}
					lastLevel = groupMua.lstLevel.get(j);
				}
			}
		}
		return errorMessage;
	}

	public String validateQuantityUnit(String promotionCode, ListGroupMua listGroupMua) {
		String errorMessage = "";
		Integer unit = null;
		for (int i = 0; i < listGroupMua.size(); i++) {
			GroupMua groupMua = listGroupMua.get(i);
			if (groupMua.lstLevel.size() == 1) {
				if (groupMua.lstLevel.get(0).lstSP.get(0).quantityUnit == null) {
					errorMessage = Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "catalog.promotion.import.type.zv22.quantity.unit", promotionCode);
					return errorMessage;
				}
			} else {
				GroupSP lastLevel = groupMua.lstLevel.get(0);
				if (lastLevel.lstSP.get(0).quantityUnit == null) {
					errorMessage = Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "catalog.promotion.import.type.zv22.quantity.unit", promotionCode);
					return errorMessage;
				} else if (unit == null) {
					unit = lastLevel.lstSP.get(0).quantityUnit;
				}
				for (int j = 1; j < groupMua.lstLevel.size(); j++) {
					if (groupMua.lstLevel.get(j).lstSP.get(0).quantityUnit == null) {
						errorMessage = Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "catalog.promotion.import.type.zv22.quantity.unit", promotionCode);
						return errorMessage;
					} else if (!groupMua.lstLevel.get(j).lstSP.get(0).quantityUnit.equals(unit)) {
						errorMessage = Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "catalog.promotion.import.type.zv22.quantity.unit.trung.du.lien", promotionCode);
						return errorMessage;
					}
					//lastLevel = groupMua.lstLevel.get(j);
				}
			}
		}
		return errorMessage;
	}

	public String validateFreeItem(String promotionCode, ListGroupKM listGroupKM) {
		String errorMessage = "";
		for (int i = 0; i < listGroupKM.size(); i++) {
			GroupKM groupKM = listGroupKM.get(i);
			if (groupKM.lstLevel.size() == 1) {
				for (int ii = 0; ii < groupKM.lstLevel.get(0).lstSP.size(); ii++) {
					if (StringUtil.isNullOrEmpty(groupKM.lstLevel.get(0).lstSP.get(ii).productCode) || groupKM.lstLevel.get(0).lstSP.get(ii).quantity == null) {
						errorMessage = Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "catalog.promotion.import.type.zv09.free.no.product.or.no.quantity", promotionCode);
						return errorMessage;
					}
				}
			} else {
				for (int j = 0; j < groupKM.lstLevel.size(); j++) {
					GroupSP level = groupKM.lstLevel.get(j);
					for (int ii = 0; ii < level.lstSP.size(); ii++) {
						if (StringUtil.isNullOrEmpty(level.lstSP.get(ii).productCode) || level.lstSP.get(ii).quantity == null) {
							errorMessage = Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "catalog.promotion.import.type.zv09.free.no.product.or.no.quantity", promotionCode);
							return errorMessage;
						}
					}
				}
			}
		}
		return errorMessage;
	}

	public String validateFreePercent(String promotionCode, ListGroupKM listGroupKM) {
		String errorMessage = "";
		for (int i = 0; i < listGroupKM.size(); i++) {
			GroupKM groupKM = listGroupKM.get(i);
			if (groupKM.lstLevel.size() == 1) {
				if (groupKM.lstLevel.get(0).lstSP.get(0).percent == null) {
					errorMessage = Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "catalog.promotion.import.type.zv01.free.no.percent", promotionCode);
				}
			} else {
				for (int j = 0; j < groupKM.lstLevel.size(); j++) {
					if (groupKM.lstLevel.get(j).lstSP.get(0).percent == null) {
						errorMessage = Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "catalog.promotion.import.type.zv01.free.no.percent", promotionCode);
					}
				}
			}
		}
		return errorMessage;
	}

	public String validateFreeAmount(String promotionCode, ListGroupKM listGroupKM) {
		String errorMessage = "";
		for (int i = 0; i < listGroupKM.size(); i++) {
			GroupKM groupKM = listGroupKM.get(i);
			if (groupKM.lstLevel.size() == 1) {
				if (groupKM.lstLevel.get(0).lstSP.get(0).amount == null) {
					errorMessage = Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "catalog.promotion.import.type.zv14.free.amount", promotionCode);
					return errorMessage;
				}
			} else {
				for (int j = 0; j < groupKM.lstLevel.size(); j++) {
					if (groupKM.lstLevel.get(j).lstSP.get(0).amount == null) {
						errorMessage = Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "catalog.promotion.import.type.zv14.free.amount", promotionCode);
						return errorMessage;
					}
				}
			}
		}
		return errorMessage;
	}

	//sap xep theo so luong va ten SP mua, cac truong free, amount, percent dc sap xep theo
	public void sortQuantityProduct(String[] arrProduct, Integer[] arrProductUnit, BigDecimal[] arrSaleQuantity, Boolean[] arrAndOr, String[] arrFreeProduct, Integer[] arrFreeProductUnit, BigDecimal[] arrFreeQuantity, BigDecimal[] arrFreeAmount,
			Float[] arrPercent) {
		boolean isAmount = false, isQuantity = false, isPercent = false;
		if (arrFreeProduct != null)
			isQuantity = true;//swap san pham + so luong KM
		if (arrFreeAmount != null)
			isAmount = true;//swap amount
		if (arrPercent != null)
			isPercent = true;//swap percent
		String name;
		Integer unit;
		Integer freeUnit;
		BigDecimal quantity;
		Boolean andOr;
		Float percent;// = 0f
		BigDecimal amount;
		if (arrProduct != null) {
			for (int i = 0; i < arrSaleQuantity.length - 1; i++) {
				if (arrSaleQuantity[i] != null) {
					for (int j = i + 1; j < arrSaleQuantity.length; j++) {
						//sap xep theo so luong roi toi productCode
						if ((arrSaleQuantity[j] != null && arrSaleQuantity[j].compareTo(arrSaleQuantity[i]) < 0) || (arrSaleQuantity[j] != null && arrSaleQuantity[j].equals(arrSaleQuantity[i]) && arrProduct[j] != null && arrProduct[j].compareTo(
								arrProduct[i]) < 0)) {
							name = arrProduct[i];
							arrProduct[i] = arrProduct[j];
							arrProduct[j] = name;
							unit = arrProductUnit[i];
							arrProductUnit[i] = arrProductUnit[j];
							arrProductUnit[j] = unit;
							quantity = arrSaleQuantity[i];
							arrSaleQuantity[i] = arrSaleQuantity[j];
							arrSaleQuantity[j] = quantity;
							andOr = arrAndOr[i];
							arrAndOr[i] = arrAndOr[j];
							arrAndOr[j] = andOr;
							if (isQuantity) {
								name = arrFreeProduct[i];
								arrFreeProduct[i] = arrFreeProduct[j];
								arrFreeProduct[j] = name;
								freeUnit = arrFreeProductUnit[i];
								arrFreeProductUnit[i] = arrFreeProductUnit[j];
								arrFreeProductUnit[j] = freeUnit;
								quantity = arrFreeQuantity[i];
								arrFreeQuantity[i] = arrFreeQuantity[j];
								arrFreeQuantity[j] = quantity;
							}
							if (isAmount) {
								amount = arrFreeAmount[i];
								arrFreeAmount[i] = arrFreeAmount[j];
								arrFreeAmount[j] = amount;
							}
							if (isPercent) {
								percent = arrPercent[i];
								arrPercent[i] = arrPercent[j];
								arrPercent[j] = percent;
							}
						}
					}
				}
			}
		}
	}

	//sap xep theo so luong va ten SP mua, cac truong free, amount, percent dc sap xep theo
	public void sortAmountProduct(String[] arrProduct, Integer[] arrProductUnit, BigDecimal[] arrSaleAmount, Boolean[] arrAndOr, String[] arrFreeProduct, Integer[] arrFreeProductUnit, BigDecimal[] arrFreeQuantity, BigDecimal[] arrFreeAmount,
			Float[] arrPercent) {
		boolean isAmount = false, isQuantity = false, isPercent = false;
		if (arrFreeProduct != null)
			isQuantity = true;//swap san pham + so luong KM
		if (arrFreeAmount != null)
			isAmount = true;//swap amount
		if (arrPercent != null)
			isPercent = true;//swap percent
		String name;
		Integer unit;
		Boolean andOr;
		Float percent;// = 0f
		BigDecimal quantity;
		BigDecimal amount;
		if (arrProduct != null) {
			for (int i = 0; i < arrSaleAmount.length - 1; i++) {
				if (arrSaleAmount[i] != null) {
					for (int j = i + 1; j < arrSaleAmount.length; j++) {
						//sap xep theo so luong roi toi productCode
						if ((arrSaleAmount[j] != null && arrSaleAmount[j].compareTo(arrSaleAmount[i]) < 0) || (arrSaleAmount[j] != null && arrSaleAmount[j].equals(arrSaleAmount[i]) && arrProduct[j] != null && arrProduct[j].compareTo(
								arrProduct[i]) < 0)) {
							name = arrProduct[i];
							arrProduct[i] = arrProduct[j];
							arrProduct[j] = name;
							unit = arrProductUnit[i];
							arrProductUnit[i] = arrProductUnit[j];
							arrProductUnit[j] = unit;
							amount = arrSaleAmount[i];
							arrSaleAmount[i] = arrSaleAmount[j];
							arrSaleAmount[j] = amount;
							andOr = arrAndOr[i];
							arrAndOr[i] = arrAndOr[j];
							arrAndOr[j] = andOr;
							if (isQuantity) {
								name = arrFreeProduct[i];
								arrFreeProduct[i] = arrFreeProduct[j];
								arrFreeProduct[j] = name;
								unit = arrFreeProductUnit[i];
								arrFreeProductUnit[i] = arrFreeProductUnit[j];
								arrFreeProductUnit[j] = unit;
								quantity = arrFreeQuantity[i];
								arrFreeQuantity[i] = arrFreeQuantity[j];
								arrFreeQuantity[j] = quantity;
							}
							if (isAmount) {
								amount = arrFreeAmount[i];
								arrFreeAmount[i] = arrFreeAmount[j];
								arrFreeAmount[j] = amount;
							}
							if (isPercent) {
								percent = arrPercent[i];
								arrPercent[i] = arrPercent[j];
								arrPercent[j] = percent;
							}
						}
					}
				}
			}
		}
	}

	//sap xep theo so luong cac truong free, amount, percent dc sap xep theo
	public void sortAmount(BigDecimal[] arrSaleAmount, Boolean[] arrAndOr, String[] arrFreeProduct, Integer[] arrFreeProductUnit, BigDecimal[] arrFreeQuantity, BigDecimal[] arrFreeAmount, Float[] arrPercent) {
		boolean isAndOr = false, isAmount = false, isQuantity = false, isPercent = false;
		if (arrAndOr != null)
			isAndOr = true;//swap san pham + so luong KM
		if (arrFreeProduct != null)
			isQuantity = true;//swap san pham + so luong KM
		if (arrFreeAmount != null)
			isAmount = true;//swap amount
		if (arrPercent != null)
			isPercent = true;//swap percent
		String name;
		Integer unit;
		Boolean andOr;
		Float percent;// = 0f
		BigDecimal quantity;
		BigDecimal amount;
		if (arrSaleAmount != null) {
			for (int i = 0; i < arrSaleAmount.length - 1; i++) {
				if (arrSaleAmount[i] != null) {
					for (int j = i + 1; j < arrSaleAmount.length; j++) {
						//sap xep theo so luong roi toi productCode
						if (arrSaleAmount[j] != null && arrSaleAmount[j].compareTo(arrSaleAmount[i]) < 0) {
							amount = arrSaleAmount[i];
							arrSaleAmount[i] = arrSaleAmount[j];
							arrSaleAmount[j] = amount;
							if (isAndOr) {
								andOr = arrAndOr[i];
								arrAndOr[i] = arrAndOr[j];
								arrAndOr[j] = andOr;
							}
							if (isQuantity) {
								name = arrFreeProduct[i];
								arrFreeProduct[i] = arrFreeProduct[j];
								arrFreeProduct[j] = name;
								unit = arrFreeProductUnit[i];
								arrFreeProductUnit[i] = arrFreeProductUnit[j];
								arrFreeProductUnit[j] = unit;
								quantity = arrFreeQuantity[i];
								arrFreeQuantity[i] = arrFreeQuantity[j];
								arrFreeQuantity[j] = quantity;
							}
							if (isAmount) {
								amount = arrFreeAmount[i];
								arrFreeAmount[i] = arrFreeAmount[j];
								arrFreeAmount[j] = amount;
							}
							if (isPercent) {
								percent = arrPercent[i];
								arrPercent[i] = arrPercent[j];
								arrPercent[j] = percent;
							}
						}
					}
				}
			}
		}
	}

	//sap xep theo so luong cac truong free, quantity, percent dc sap xep theo
	public void sortQuantity(BigDecimal[] arrSaleQuantity, Boolean[] arrAndOr, String[] arrFreeProduct, Integer[] arrFreeProductUnit, BigDecimal[] arrFreeQuantity, BigDecimal[] arrFreeAmount, Float[] arrPercent) {
		boolean isAndOr = false, isAmount = false, isQuantity = false, isPercent = false;
		if (arrAndOr != null)
			isAndOr = true;//swap san pham + so luong KM
		if (arrFreeProduct != null)
			isQuantity = true;//swap san pham + so luong KM
		if (arrFreeAmount != null)
			isAmount = true;//swap amount
		if (arrPercent != null)
			isPercent = true;//swap percent
		String name;
		Integer unit;
		Boolean andOr;
		Float percent;//0f
		BigDecimal quantity;
		BigDecimal amount;
		if (arrSaleQuantity != null) {
			for (int i = 0; i < arrSaleQuantity.length - 1; i++) {
				if (arrSaleQuantity[i] != null) {
					for (int j = i + 1; j < arrSaleQuantity.length; j++) {
						//sap xep theo so luong roi toi productCode
						if (arrSaleQuantity[j] != null && arrSaleQuantity[j].compareTo(arrSaleQuantity[i]) < 0) {
							quantity = arrSaleQuantity[i];
							arrSaleQuantity[i] = arrSaleQuantity[j];
							arrSaleQuantity[j] = quantity;
							if (isAndOr) {
								andOr = arrAndOr[i];
								arrAndOr[i] = arrAndOr[j];
								arrAndOr[j] = andOr;
							}
							if (isQuantity) {
								name = arrFreeProduct[i];
								arrFreeProduct[i] = arrFreeProduct[j];
								arrFreeProduct[j] = name;
								unit = arrFreeProductUnit[i];
								arrFreeProductUnit[i] = arrFreeProductUnit[j];
								arrFreeProductUnit[j] = unit;
								quantity = arrFreeQuantity[i];
								arrFreeQuantity[i] = arrFreeQuantity[j];
								arrFreeQuantity[j] = quantity;
							}
							if (isAmount) {
								amount = arrFreeAmount[i];
								arrFreeAmount[i] = arrFreeAmount[j];
								arrFreeAmount[j] = amount;
							}
							if (isPercent) {
								percent = arrPercent[i];
								arrPercent[i] = arrPercent[j];
								arrPercent[j] = percent;
							}
						}
					}
				}
			}
		}
	}

	/**
	 * Kiem tra cac dong duplicate TungMT
	 * 
	 * @param type
	 * @return
	 */
	public String checkDuplicate(String type, List<Row> rowIter, Row myRow) {
		String messageError = "";
		String productCode = "", freeProductCode = "";
		Integer quantity = null;
		BigDecimal amount = null;
		try {
			if (myRow.getCell(2) != null && StringUtil.isNullOrEmpty(messageError)) {
				productCode = getCellValueToString(myRow.getCell(2));
			}
			if (myRow.getCell(3) != null) {
				if (myRow.getCell(3).getCellType() == Cell.CELL_TYPE_NUMERIC) {
					quantity = (int) myRow.getCell(3).getNumericCellValue();
				}
			}
			if (myRow.getCell(5) != null) {
				if (myRow.getCell(5).getCellType() == Cell.CELL_TYPE_NUMERIC) {
					amount = BigDecimal.valueOf(myRow.getCell(5).getNumericCellValue());
				}
			}
			if (myRow.getCell(8) != null) {
				freeProductCode = getCellValueToString(myRow.getCell(8));
			}
		} catch (Exception e) {
			messageError += R.getResource("system.error");
			LogUtility.logError(e, e.getMessage());
		}
		for (int i = 0; i < rowIter.size(); i++) {//for tu 0 den dong truoc dong dang xet 
			Row row = rowIter.get(i);
			if (PromotionType.ZV01.getValue().equals(type) || PromotionType.ZV02.getValue().equals(type) || PromotionType.ZV07.getValue().equals(type) || PromotionType.ZV08.getValue().equals(type)) {
				//trung spmua va sl mua
				if (productCode.equals(getCellValueToString(row.getCell(2))) && row.getCell(3).getCellType() == Cell.CELL_TYPE_NUMERIC && quantity != null && quantity.equals((int) row.getCell(3).getNumericCellValue())) {
					messageError += R.getResource("catalog.promotion.import.duplicate.sp.sl");
					break;
				}
			} else if (PromotionType.ZV04.getValue().equals(type) || PromotionType.ZV05.getValue().equals(type) || PromotionType.ZV10.getValue().equals(type) || PromotionType.ZV11.getValue().equals(type) || PromotionType.ZV16.getValue().equals(type)
					|| PromotionType.ZV17.getValue().equals(type)) {
				//trung spmua va amount
				if (productCode.equals(getCellValueToString(row.getCell(2))) && row.getCell(5).getCellType() == Cell.CELL_TYPE_NUMERIC && amount != null && amount.equals(BigDecimal.valueOf(row.getCell(5).getNumericCellValue()))) {
					messageError += R.getResource("catalog.promotion.import.duplicate.sp.st");
					break;
				}
			} else if (PromotionType.ZV03.getValue().equals(type) || PromotionType.ZV09.getValue().equals(type)) {
				//trung spmua va sl mua va spkm
				if (productCode.equals(getCellValueToString(row.getCell(2))) && freeProductCode.equals(getCellValueToString(row.getCell(8))) && row.getCell(3).getCellType() == Cell.CELL_TYPE_NUMERIC && quantity != null && quantity.equals((int) row
						.getCell(3).getNumericCellValue())) {
					messageError += R.getResource("catalog.promotion.import.duplicate.sp.sl.spkm");
					break;
				}
			} else if (PromotionType.ZV06.getValue().equals(type) || PromotionType.ZV12.getValue().equals(type) || PromotionType.ZV18.getValue().equals(type)) {
				//trung spmua va amount va spkm
				if (productCode.equals(getCellValueToString(row.getCell(2))) && freeProductCode.equals(getCellValueToString(row.getCell(8))) && row.getCell(5).getCellType() == Cell.CELL_TYPE_NUMERIC && amount != null && amount.equals(BigDecimal
						.valueOf(row.getCell(5).getNumericCellValue()))) {
					messageError += R.getResource("catalog.promotion.import.duplicate.sp.st.spkm");
					break;
				}
			} else if (PromotionType.ZV19.getValue().equals(type) || PromotionType.ZV20.getValue().equals(type)) {
				//trung amount
				if (row.getCell(5).getCellType() == Cell.CELL_TYPE_NUMERIC && amount != null && amount.equals(BigDecimal.valueOf(row.getCell(5).getNumericCellValue()))) {
					messageError += R.getResource("catalog.promotion.import.duplicate.st");
					break;
				}
			} else if (PromotionType.ZV21.getValue().equals(type)) {
				//trung amount va SPKM
				if (freeProductCode.equals(getCellValueToString(row.getCell(8))) && row.getCell(5).getCellType() == Cell.CELL_TYPE_NUMERIC && amount != null && amount.equals(BigDecimal.valueOf(row.getCell(5).getNumericCellValue()))) {
					messageError += R.getResource("catalog.promotion.import.duplicate.st.spkm");
					break;
				}
			}
		}
		return messageError;
	}

	/**
	 * Kiem tra cac cot co can thiet khong TungMT
	 * 
	 * @param type
	 * @param col
	 *            : 2.productCode, 3.SaleQty, 5.SaleAmt, 6.DisAmount,
	 *            7.DisPercent, 8.FreeItemCode, 9.FreeQty, 11.AndOr
	 * @return true: can cho CTKM, false: khong can cho CTKM
	 */
	public Boolean checkColumnNecessary(String type, int col) {
		if (col == 2) {//productCode
			//Neu CTKM dang doc thi khong can productCode
			if (PromotionType.ZV19.getValue().equals(type) || PromotionType.ZV20.getValue().equals(type) || PromotionType.ZV21.getValue().equals(type) || PromotionType.ZV22.getValue().equals(type) || PromotionType.ZV23.getValue().equals(type)
					|| PromotionType.ZV24.getValue().equals(type)) {
				return false;
			} else {
				return true;
			}
		} else if (col == 3) {//sale quantity
			if (PromotionType.ZV01.getValue().equals(type) || PromotionType.ZV02.getValue().equals(type) || PromotionType.ZV03.getValue().equals(type) || PromotionType.ZV07.getValue().equals(type) || PromotionType.ZV08.getValue().equals(type)
					|| PromotionType.ZV09.getValue().equals(type) || PromotionType.ZV13.getValue().equals(type) || PromotionType.ZV14.getValue().equals(type) || PromotionType.ZV15.getValue().equals(type) || PromotionType.ZV22.getValue().equals(type)
					|| PromotionType.ZV23.getValue().equals(type) || PromotionType.ZV24.getValue().equals(type)) {
				return true;
			} else {
				return false;
			}
		} else if (col == 5) {//sale amt nguoc voi quantity
			if (PromotionType.ZV01.getValue().equals(type) || PromotionType.ZV02.getValue().equals(type) || PromotionType.ZV03.getValue().equals(type) || PromotionType.ZV07.getValue().equals(type) || PromotionType.ZV08.getValue().equals(type)
					|| PromotionType.ZV09.getValue().equals(type) || PromotionType.ZV13.getValue().equals(type) || PromotionType.ZV14.getValue().equals(type) || PromotionType.ZV15.getValue().equals(type) || PromotionType.ZV22.getValue().equals(type)
					|| PromotionType.ZV23.getValue().equals(type) || PromotionType.ZV24.getValue().equals(type)) {
				return false;
			} else {
				return true;
			}
		} else if (col == 6) {//discAmount
			if (PromotionType.ZV02.getValue().equals(type) || PromotionType.ZV05.getValue().equals(type) || PromotionType.ZV08.getValue().equals(type) || PromotionType.ZV11.getValue().equals(type) || PromotionType.ZV14.getValue().equals(type)
					|| PromotionType.ZV17.getValue().equals(type) || PromotionType.ZV20.getValue().equals(type) || PromotionType.ZV23.getValue().equals(type)) {
				return true;
			} else {
				return false;
			}
		} else if (col == 7) {//discPercent
			if (PromotionType.ZV01.getValue().equals(type) || PromotionType.ZV04.getValue().equals(type) || PromotionType.ZV07.getValue().equals(type) || PromotionType.ZV10.getValue().equals(type) || PromotionType.ZV13.getValue().equals(type)
					|| PromotionType.ZV16.getValue().equals(type) || PromotionType.ZV19.getValue().equals(type) || PromotionType.ZV22.getValue().equals(type)) {
				return true;
			} else {
				return false;
			}
		} else if (col == 8 || col == 9) {//freeItemCode and FreeQty
			if (PromotionType.ZV03.getValue().equals(type) || PromotionType.ZV06.getValue().equals(type) || PromotionType.ZV09.getValue().equals(type) || PromotionType.ZV12.getValue().equals(type) || PromotionType.ZV15.getValue().equals(type)
					|| PromotionType.ZV18.getValue().equals(type) || PromotionType.ZV21.getValue().equals(type) || PromotionType.ZV24.getValue().equals(type)) {
				return true;
			} else {
				return false;
			}
		}
		return true;
	}

	List<ExcelPromotionHeader> listHeader;
	List<ExcelPromotionDetail> listDetail;
	List<ExcelPromotionUnit> listUnit;
	Map<String, ExcelPromotionHeader> mapHeader;
	Map<String, ExcelPromotionUnit> mapUnit;
	Map<String, String> mapErrorPromotion;
	Map<String, String> mapErrorUnit;
	Map<String, String> mapType;
	Map<String, ListGroupMua> mapPromotionMua;
	Map<String, ListGroupKM> mapPromotionKM;
	Map<String, String> mapPromotionTypeCheck;
	MapMuaKM mapMuaKM;
	private List<PromotionShopVO> lstPromotionShop;
	private List<CellBean> lstPromotionShopError;
	private final String ERR_NUM_SHEET = "ERR_NUM_SHEET";

	private String getCellValueToString(Cell cell) {
		String value;// = "";
		if(cell==null)
		{
		 return "";	
		}
		switch (cell.getCellType()) {
		case Cell.CELL_TYPE_BLANK:
			value = "";
			break;
		case Cell.CELL_TYPE_NUMERIC:
			if (HSSFDateUtil.isCellDateFormatted(cell)) {
				value = DateUtil.toDateString(cell.getDateCellValue(), DateUtil.DATE_FORMAT_DDMMYYYY);
			} else if (BigDecimal.valueOf(Double.valueOf(cell.getNumericCellValue())).compareTo(BigDecimal.valueOf(Double.valueOf(cell.getNumericCellValue()).longValue())) == 0) {
				value = String.valueOf(BigDecimal.valueOf(Double.valueOf(cell.getNumericCellValue())).longValue());
			} else {
				value = String.valueOf(cell.getNumericCellValue());
			}
			break;
		case Cell.CELL_TYPE_STRING:
			value = cell.getStringCellValue();
			break;
		default:
			value = cell.toString();
			break;
		}

		return value;
	}
	
	/**
	 * Doc du lieu tap tin Excel Import CTKM
	 * 
	 * @author hunglm16
	 * @throws Exception
	 * @since 13/09/2015
	 * @description Cap nhat Code (Mager)
	 */
	private void getExcelData21ZV() throws Exception {
		listHeader = new ArrayList<ExcelPromotionHeader>();
		listDetail = new ArrayList<ExcelPromotionDetail>();
		listUnit = new ArrayList<ExcelPromotionUnit>();
		mapHeader = new HashMap<String, ExcelPromotionHeader>();
		mapUnit = new HashMap<String, ExcelPromotionUnit>();
		mapErrorPromotion = new HashMap<String, String>();
		mapType = new HashMap<String, String>();
		mapPromotionMua = new HashMap<String, ListGroupMua>();
		mapPromotionKM = new HashMap<String, ListGroupKM>();
		mapMuaKM = new MapMuaKM();
		mapPromotionTypeCheck = new HashMap<String, String>();
		lstHeaderError = new ArrayList<CellBean>();
		lstDetailError = new ArrayList<CellBean>();
		listUnitError = new ArrayList<CellBean>();
		Map<String, Integer> mapCheckHeaderDuplicate = new HashMap<String, Integer>();
		Map<String, String> mapCheckType = apParamMgr.getMapPromotionType();
		Workbook myWorkBook = null;
		InputStream is = new FileInputStream(excelFile);
		int MAX_ARRAY = 10000;
		final int NUM_SHEETS = 2;

		if (!is.markSupported()) {
			is = new PushbackInputStream(is, 8);
		}
		if (POIFSFileSystem.hasPOIFSHeader(is)) {
			myWorkBook = new HSSFWorkbook(is);
		} else if (POIXMLDocument.hasOOXMLHeader(is)) {
			myWorkBook = new XSSFWorkbook(OPCPackage.open(is));
		}
		if (myWorkBook != null) {
			if (myWorkBook.getNumberOfSheets() < NUM_SHEETS) {
				throw new Exception(ERR_NUM_SHEET);
			}
			Sheet headerSheet = myWorkBook.getSheetAt(0);
			Sheet detailSheet = myWorkBook.getSheetAt(1);
			Sheet unitSheet = myWorkBook.getSheetAt(2);
			int iRun = 0;
			totalItem = 0;
			int maxSizeSheet1 = 12;
			if (headerSheet != null) {
				Iterator<?> rowIter = headerSheet.rowIterator();
				while (rowIter.hasNext()) {
					Row myRow = (Row) rowIter.next();
					if (iRun == 0) {
						iRun++;
						continue;
					}
					boolean isContinue = true;
					//Kiem tra su hop le cua Row Import
					for (int i = 0; i < maxSizeSheet1; i++) {
						if (myRow.getCell(i) != null && !StringUtil.isNullOrEmpty(getCellValueToString(myRow.getCell(i)))) {
							isContinue = false;
							break;
						}
					}
					if (isContinue) {
						continue;
					}
					ExcelPromotionHeader header = new ExcelPromotionHeader();
					CellBean errRow = new CellBean();
					String messageError = "";
					totalItem++;
					//0	get promotionCode
					String promotionCode = "";
					try {
						Cell cellPromotionCode = myRow.getCell(0);
						if (cellPromotionCode != null && StringUtil.isNullOrEmpty(messageError)) {
							//						try {
							//							promotionCode = cellPromotionCode.getStringCellValue();
							//						} catch (Exception e) {
							//							promotionCode = String.valueOf(cellPromotionCode.getNumericCellValue());
							//							LogUtility.logError(e, e.getMessage());
							//						}
							promotionCode = getCellValueToString(cellPromotionCode);
							promotionCode = promotionCode != null ? promotionCode.toUpperCase().trim() : "";
							header.promotionCode = promotionCode;
							errRow.setContent1(promotionCode);
							if (StringUtil.isNullOrEmpty(promotionCode)) {
								messageError += R.getResource("catalog.promotion.import.column.null", "Mã CTKM");
							} else {
								messageError += ValidateUtil.validateField(promotionCode, "catalog.promotion.import.column.progcode", 50, ConstantManager.ERR_REQUIRE, ConstantManager.ERR_MAX_LENGTH, ConstantManager.ERR_EXIST_SPECIAL_CHAR_IN_CODE);
							}
							if (mapCheckHeaderDuplicate.get(promotionCode) != null) {
								messageError += Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "promotion.product.import.duplicate", mapCheckHeaderDuplicate.get(promotionCode));
								messageError += "\n";
							} else {
								mapCheckHeaderDuplicate.put(promotionCode, myRow.getRowNum());
							}
							PromotionProgram existPromotion = promotionProgramMgr.getPromotionProgramByCode(promotionCode);
							if (existPromotion != null && !ActiveType.WAITING.equals(existPromotion.getStatus())) {
								messageError += Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "catalog.promotion.program.exists");
								messageError += "\n";
							}
						}
					} catch (Exception e) {
						messageError += R.getResource("catalog.promotion.import.get.promotion.error", promotionCode);
						LogUtility.logError(e, e.getMessage());
						//						messageError += Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "catalog.promotion.import.cant.read.cell.string", iRun, "ProgCode");
					}
					//1	get description
					try {
						Cell cellDescription = myRow.getCell(1);
						if (cellDescription != null && StringUtil.isNullOrEmpty(messageError)) {
							String description = getCellValueToString(cellDescription);
							header.description = description;
							errRow.setContent2(description);
							messageError += ValidateUtil.validateField(description, "catalog.promotion.import.column.progpescr", 100, ConstantManager.ERR_REQUIRE, ConstantManager.ERR_MAX_LENGTH, ConstantManager.ERR_EXIST_SPECIAL_CHAR_IN_NAME);
						}
					} catch (Exception e) {
						messageError += Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "catalog.promotion.import.cant.read.cell.string", iRun, "ProgDescr");
						LogUtility.logError(e, e.getMessage());
					}
					//2	get release
					try {
						if (myRow.getCell(2) != null && StringUtil.isNullOrEmpty(messageError)) {
							errRow.setContent3(getCellValueToString(myRow.getCell(2)));
						}
					} catch (Exception e) {
						messageError += Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "catalog.promotion.import.cant.read.cell.number", iRun, "Release");
						LogUtility.logError(e, e.getMessage());
					}
					//3	get promotion type
					String type = null;
					try {
						Cell cellPromotionType = myRow.getCell(3);
						if (cellPromotionType != null && StringUtil.isNullOrEmpty(messageError)) {
							type = getCellValueToString(cellPromotionType);
							type = type != null ? type.toUpperCase().trim() : "";
							header.type = type;
							errRow.setContent4(type);
							if (StringUtil.isNullOrEmpty(type)) {
								messageError += Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "common.required", Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "catalog.promotion.type"));
								messageError += "\n";
							}
							if (mapCheckType.get(type) == null) {
								messageError += Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "catalog.exists.before", Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "catalog.promotion.type"));
								messageError += "\n";
							}
							mapType.put(header.promotionCode, type);
						}
					} catch (Exception e) {
						messageError += Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "catalog.promotion.import.cant.read.cell.string", iRun, "ConditionTypeCode");
						LogUtility.logError(e, e.getMessage());
					}
					//4 get format
					//try {
					//	Cell cellFormat = myRow.getCell(4);
					//	if (cellFormat != null && StringUtil.isNullOrEmpty(messageError)) {
					//		String format = cellFormat.getStringCellValue();
					//		header.format = format;
					//		errRow.setContent5(format);
					//	}
					//} catch (Exception e) {
					//	messageError += Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "catalog.promotion.import.cant.read.cell.string", iRun, "User1");
					//}
					//4 get fromDate
					try {
						Cell cellFromDate = myRow.getCell(4);
						if (cellFromDate != null && StringUtil.isNullOrEmpty(messageError)) {
							if (cellFromDate.getCellType() == Cell.CELL_TYPE_NUMERIC && cellFromDate.getCellStyle() != null && DateUtil.HSSF_DATE_FORMAT_M_D_YY.equals(cellFromDate.getCellStyle().getDataFormatString())) {
								if (cellFromDate.getDateCellValue() != null || !StringUtil.isNullOrEmpty(cellFromDate.getStringCellValue())) {
									String __fromDate = DateUtil.toDateString(cellFromDate.getDateCellValue(), DateUtil.DATE_FORMAT_DDMMYYYY);
									Date fromDate = DateUtil.toDate(__fromDate, DateUtil.DATE_FORMAT_DDMMYYYY);
									header.fromDate = fromDate;
									errRow.setContent5(__fromDate);
								} else {
									messageError += Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "common.required", Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "imp.epx.tuyen.clmn.tuNgay"));
									messageError += "\n";
								}
							} else if (cellFromDate.getCellType() == Cell.CELL_TYPE_NUMERIC && cellFromDate.getCellStyle() != null && DateUtil.DATE_FORMAT_VISIT.equals(cellFromDate.getCellStyle().getDataFormatString())) {
								if (cellFromDate.getDateCellValue() != null || !StringUtil.isNullOrEmpty(cellFromDate.getStringCellValue())) {
									String __fromDate = DateUtil.toDateString(cellFromDate.getDateCellValue(), DateUtil.DATE_FORMAT_VISIT);
									Date fromDate = DateUtil.toDate(__fromDate, DateUtil.DATE_FORMAT_VISIT);
									header.fromDate = fromDate;
									errRow.setContent5(__fromDate);
								} else {
									messageError += Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "common.required", Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "imp.epx.tuyen.clmn.tuNgay"));
									messageError += "\n";
								}
							} else {
								if (!StringUtil.isNullOrEmpty(cellFromDate.getStringCellValue())) {
									try {
										String __fromDate = cellFromDate.getStringCellValue();
										if (DateUtil.checkInvalidFormatDate(__fromDate)) {
											messageError += R.getResource("common.invalid.format.date", R.getResource("imp.epx.tuyen.clmn.tuNgay"));
											messageError += "\n";
											errRow.setContent5(__fromDate);
										} else {
											Date fromDate = DateUtil.toDate(__fromDate, DateUtil.DATE_FORMAT_DDMMYYYY);
											header.fromDate = fromDate;
											errRow.setContent5(__fromDate);
										}
									} catch (Exception e1) {
										messageError += R.getResource("common.invalid.format.date", R.getResource("imp.epx.tuyen.clmn.tuNgay"));
										messageError += "\n";
										errRow.setContent5(cellFromDate.getStringCellValue());
										LogUtility.logError(e1, e1.getMessage());
									}
								} else {
									messageError += R.getResource("common.required", R.getResource("imp.epx.tuyen.clmn.tuNgay"));
									messageError += "\n";
								}
							}
						}
					} catch (Exception e) {
						messageError += R.getResource("catalog.promotion.import.cant.read.cell.date", iRun, "FromDate");
						LogUtility.logError(e, e.getMessage());
					}
					//5 get toDate
					try {
						Cell cellToDate = myRow.getCell(5);
						if (cellToDate != null && StringUtil.isNullOrEmpty(messageError)) {
							if (cellToDate.getCellType() == Cell.CELL_TYPE_NUMERIC && cellToDate.getCellStyle() != null && DateUtil.HSSF_DATE_FORMAT_M_D_YY.equals(cellToDate.getCellStyle().getDataFormatString())) {
								if (cellToDate.getDateCellValue() != null || !StringUtil.isNullOrEmpty(cellToDate.getStringCellValue())) {
									String __toDate = DateUtil.toDateString(cellToDate.getDateCellValue(), DateUtil.DATE_FORMAT_DDMMYYYY);
									Date toDate = DateUtil.toDate(__toDate, DateUtil.DATE_FORMAT_DDMMYYYY);
									header.toDate = toDate;
									errRow.setContent6(__toDate);
								}
							} else if (cellToDate.getCellType() == Cell.CELL_TYPE_NUMERIC && cellToDate.getCellStyle() != null && DateUtil.DATE_FORMAT_VISIT.equals(cellToDate.getCellStyle().getDataFormatString())) {
								if (cellToDate.getDateCellValue() != null || !StringUtil.isNullOrEmpty(cellToDate.getStringCellValue())) {
									String __toDate = DateUtil.toDateString(cellToDate.getDateCellValue(), DateUtil.DATE_FORMAT_VISIT);
									Date toDate = DateUtil.toDate(__toDate, DateUtil.DATE_FORMAT_VISIT);
									header.toDate = toDate;
									errRow.setContent6(__toDate);
								}
							} else if (!StringUtil.isNullOrEmpty(cellToDate.getStringCellValue())) {
								try {
									String __toDate = cellToDate.getStringCellValue();
									if (DateUtil.checkInvalidFormatDate(__toDate)) {
										messageError += R.getResource("common.invalid.format.date", R.getResource("imp.epx.tuyen.clmn.denNgay"));
										messageError += "\n";
										errRow.setContent6(__toDate);
									} else {
										Date toDate = DateUtil.toDate(__toDate, DateUtil.DATE_FORMAT_DDMMYYYY);
										header.toDate = toDate;
										errRow.setContent6(__toDate);
									}
								} catch (Exception e1) {
									messageError += R.getResource("common.invalid.format.date", R.getResource("imp.epx.tuyen.clmn.denNgay"));
									messageError += "\n";
									errRow.setContent6(cellToDate.getStringCellValue());
									LogUtility.logError(e1, e1.getMessage());
								}
							}
						}
						if (header.fromDate != null && header.toDate != null) {
							if (DateUtil.compareDateWithoutTime(header.fromDate, header.toDate) > 0) {
								messageError += R.getResource("common.fromdate.greater.todate") + "\n";
							}
						}
					} catch (Exception e) {
						messageError += R.getResource("catalog.promotion.import.cant.read.cell.date", iRun, "ToDate");
						LogUtility.logError(e, e.getMessage());
					}

					//6 Số thông báo CTKM
					/*
					 * String description =
					 * getCellValueToString(cellDescription); header.description
					 * = description; errRow.setContent2(description);
					 * messageError += ValidateUtil.validateField(description,
					 * "catalog.promotion.import.column.progpescr", 100,
					 * ConstantManager.ERR_REQUIRE,
					 * ConstantManager.ERR_MAX_LENGTH,
					 * ConstantManager.ERR_EXIST_SPECIAL_CHAR_IN_NAME);
					 */
					
					/*errMsg = ValidateUtil.validateField(promotionName, "catalog.promotion.name", 500, ConstantManager.ERR_REQUIRE, ConstantManager.ERR_EXIST_SPECIAL_CHAR_IN_SPECIAL, ConstantManager.ERR_MAX_LENGTH);
					if (StringUtil.isNullOrEmpty(errMsg)) {
						errMsg = ValidateUtil.validateField(description, "common.description", null, ConstantManager.ERR_EXIST_SPECIAL_CHAR_IN_SPECIAL);
					}*/
					
					// 6 Số thông báo
					try {
						Cell cellNoticeCode = myRow.getCell(6);
						if (cellNoticeCode != null && StringUtil.isNullOrEmpty(messageError)) {
							String noticeCode = getCellValueToString(cellNoticeCode);
							if(noticeCode != null){
								noticeCode = noticeCode.trim();
								noticeCode = noticeCode.toUpperCase();
							}
							messageError = ValidateUtil.validateField(noticeCode, "catalog.promotion.noticecode", 100, ConstantManager.ERR_EXIST_SPECIAL_CHAR_IN_SPECIAL);
							/*if(noticeCode.length() > 100){
								messageError += R.getResource("catalog.promotion.import.notice.code.over.length")+"\n";
							}else*/ 
							if(StringUtil.isNullOrEmpty(noticeCode)) {
								messageError += R.getResource("catalog.promotion.import.notice.code.obligate") + "\n";
							}else if(noticeCode != null && noticeCode.trim().length() > 100){
								//messageError += R.getResource("catalog.promotion.import.notice.code.incorrect.format");
								messageError += R.getResource("catalog.promotion.import.over.max.length") + "\n";
								messageError = messageError.replaceAll("%max%", "100");
								messageError = messageError.replaceAll("%colName%", "Số thông báo");
							}else if(StringUtil.isNullOrEmpty(messageError)){
								
								header.noticeCode = noticeCode;
							}
							errRow.setContent7(noticeCode);
						}else if (cellNoticeCode == null) {
							messageError += R.getResource("catalog.promotion.import.notice.code.obligate") + "\n";
						} 
					} catch (Exception e) {
						messageError += R.getResource("catalog.promotion.import.cant.read.cell.string", iRun, "NoticeCode");
						LogUtility.logError(e, e.getMessage());
					}
					// 7 Nhóm/Tên SP hàng bán
					try {
						Cell cellDesProduct = myRow.getCell(7);
						if (cellDesProduct != null && StringUtil.isNullOrEmpty(messageError)) {
							String descProduct = getCellValueToString(cellDesProduct);
							if(descProduct != null){
								descProduct = descProduct.trim();
							}
							//descProduct = descProduct.trim();
							messageError = ValidateUtil.validateField(descProduct, "catalog.promotion.descriptionproduct", 1000, ConstantManager.ERR_EXIST_SPECIAL_CHAR_IN_SPECIAL);
							/*if(descProduct.length() > 1000){
								messageError += R.getResource("catalog.promotion.import.desc.product.over.length")+"\n";
							}else*/ 
							if(StringUtil.isNullOrEmpty(descProduct)) {
								messageError += R.getResource("catalog.promotion.import.description.product.obligate") + "\n";
							}else if(descProduct.trim().length() > 1000){
								//messageError += R.getResource("catalog.promotion.import.description.product.incorrect.format") + "\n";
								messageError += R.getResource("catalog.promotion.import.over.max.length") + "\n";
								messageError = messageError.replaceAll("%max%", "1000");
								messageError = messageError.replaceAll("%colName%", "Nhóm/Tên SP hàng bán");
							}
							else if(StringUtil.isNullOrEmpty(messageError)){
								descProduct = descProduct.trim();
								header.decriptionProduct = descProduct;
							}							
							errRow.setContent8(descProduct);
						}else if(cellDesProduct == null) {
							messageError += R.getResource("catalog.promotion.import.description.product.obligate") + "\n";
						} 

					} catch (Exception e) {
						messageError += R.getResource("catalog.promotion.import.cant.read.cell.string", iRun, "DescProduct");
						LogUtility.logError(e, e.getMessage());
					}

					// 8 Mô tả chương trình
					try {
						Cell cellDescription = myRow.getCell(8);
						if (cellDescription != null && StringUtil.isNullOrEmpty(messageError)) {
							String description = getCellValueToString(cellDescription);
							if(description != null){
								description = description.trim();
							}
							if(description != null && description.trim().length() > 1000){
								//messageError += R.getResource("catalog.promotion.import.description.program.incorrect.format") + "\n";
								//messageError += R.getResource("", iRun, "Description");
								messageError += R.getResource("catalog.promotion.import.over.max.length") + "\n";
								messageError = messageError.replaceAll("%max%", "1000");
								messageError = messageError.replaceAll("%colName%", "Mô tả chương trình");
							}else{
								description = description.trim();
								header.descriptionProgram = description;
								errRow.setContent9(description);
							}
						}
						
						/*else if(cellDescription != null){
							String description = getCellValueToString(cellDescription);
							errRow.setContent9(description);
						}*/
						
					} catch (Exception e) {
						messageError += R.getResource("catalog.promotion.import.cant.read.cell.string", iRun, "Description");
						LogUtility.logError(e, e.getMessage());
					}
					//9 Bội số
					try {
						Cell cellMultiple = myRow.getCell(9);
						header.multiple = 0; 
						if (cellMultiple != null && StringUtil.isNullOrEmpty(messageError)) {
							String multiple = getCellValueToString(cellMultiple);
							ApParam apParam = apParamMgr.getApParamByCode("LIST_PROMO_ALLOW_MULTIPLE", ApParamType.LIST_PROMO_ALLOW_MULTIPLE);
							String strListPromo = apParam != null?apParam.getValue() : "ZV02,ZV03,ZV05,ZV06,ZV08,ZV09,ZV11,ZV12,ZV13,ZV14,ZV15,ZV16,ZV17,ZV18,ZV20,ZV21,ZV23,ZV24";
							if ((Constant.IS_MULTIPLE.equals(multiple) || Constant.NON_MULTIPLE.equals(multiple)) 
									&& strListPromo != null) {
								strListPromo = strListPromo.replace(" ", "");
								strListPromo = strListPromo.toUpperCase();
								String[] ListPromoAllow = strListPromo.split(",");
								if (Constant.IS_MULTIPLE.equals(multiple) 
									 &&	!Arrays.asList(ListPromoAllow).contains(header.type.toUpperCase())) {
									System.out.println(header.type.toUpperCase());
									System.out.println(strListPromo);									
									messageError += Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "catalog.promotion.import.multiple.not.use") + "\n";
								} else {
									header.multiple = Integer.parseInt(multiple.trim());
								}
							} else {
								messageError += Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "catalog.promotion.import.multiple.incorrect.format") + "\n";
							}
							errRow.setContent10(multiple);
						}/*else if(cellMultiple != null){
							String multiple = getCellValueToString(cellMultiple);
							errRow.setContent10(multiple);
						}*/

					} catch (Exception e) {
						messageError += Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "catalog.promotion.import.cant.read.cell.number", iRun, "Multiple", "['','X']");
						LogUtility.logError(e, e.getMessage());
					}
					// 10 Tối ưu
					try {
						Cell cellRecursive = myRow.getCell(10);
						header.recursive = 0;
						if (cellRecursive != null && StringUtil.isNullOrEmpty(messageError)) {
							String recursive = getCellValueToString(cellRecursive);
							ApParam apParam = apParamMgr.getApParamByCode("LIST_PROMO_ALLOW_RECURSIVE", ApParamType.LIST_PROMO_ALLOW_RECURSIVE);
							String strListPromo = apParam == null? "ZV02,ZV03,ZV05,ZV06,ZV08,ZV09,ZV11,ZV12,ZV13,ZV14,ZV15,ZV16,ZV17,ZV18,ZV20,ZV21,ZV23,ZV24":apParam.getValue();
							if ((Constant.IS_RECURSIVE.equals(recursive) || Constant.NON_RECURSIVE.equals(recursive)) 
									&& strListPromo != null) {
								strListPromo = strListPromo.replace(" ", "");
								String[] listPromoAllow = strListPromo.split(",");
								if (Constant.IS_RECURSIVE.equals(recursive)			
									 &&	!Arrays.asList(listPromoAllow).contains(header.type.toUpperCase())) {
								/*	System.out.println(header.type.toUpperCase());
									System.out.println(strListPromo);*/
									messageError += Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "catalog.promotion.import.recursive.not.use") + "\n";
								} else {
									header.recursive = Integer.parseInt(recursive.trim());
								}
							} else {
								messageError += Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "catalog.promotion.import.recursive.incorrect.format") + "\n";
							}
							errRow.setContent11(recursive);
						}/*else if(cellRecursive != null){
							String recursive = getCellValueToString(cellRecursive);
							errRow.setContent11(recursive);
						}*/
					} catch (Exception e) {
						messageError += Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "catalog.promotion.import.cant.read.cell.number", iRun, "Recursive", "['','X']");
						LogUtility.logError(e, e.getMessage());
					}
					// 9 Loại trả thưởng
				/*	try {
						Cell cellRewardType = myRow.getCell(9);
						ApParam apParam = apParamMgr.getApParamByCode("LIST_ALLOW_REWARD_TYPE", ApParamType.LIST_ALLOW_REWARD_TYPE);
						String strListAllowReward = apParam.getApParamName();
						strListAllowReward = strListAllowReward.replace(" ", "");
						String[] listAllowReward = strListAllowReward.split(",");
						if (cellRewardType != null && StringUtil.isNullOrEmpty(messageError)) {
							String strRewardType = getCellValueToString(cellRewardType);
							if (strRewardType != null 
									&& (Constant.DISCOUNT_MONEY.equals(strRewardType) || Constant.VOUCHER.equals(strRewardType)) 
									&& Arrays.asList(listAllowReward).contains(header.type)) {
								header.rewardType = Integer.parseInt(strRewardType.trim());
							}
							errRow.setContent10(strRewardType);
						}else if(cellRewardType != null){
							String strRewardType = getCellValueToString(cellRewardType);
							errRow.setContent10(strRewardType);
						}
					} catch (Exception e) {
						messageError += R.getResource("catalog.promotion.import.cant.read.cell.number", iRun, "RewardType");
						LogUtility.logError(e, e.getMessage());
					}
					// 10 Từ ngày trả thưởng
					try {
						Cell cellFromDateReward = myRow.getCell(10);
						if (Constant.VOUCHER.equals(header.rewardType+"")) {
							if (cellFromDateReward != null && StringUtil.isNullOrEmpty(messageError)) {
								if (cellFromDateReward.getCellType() == Cell.CELL_TYPE_NUMERIC && cellFromDateReward.getCellStyle() != null && DateUtil.HSSF_DATE_FORMAT_M_D_YY.equals(cellFromDateReward.getCellStyle().getDataFormatString())) {
									if (cellFromDateReward.getDateCellValue() != null || !StringUtil.isNullOrEmpty(cellFromDateReward.getStringCellValue())) {
										String _fromDateReward = DateUtil.toDateString(cellFromDateReward.getDateCellValue(), DateUtil.DATE_FORMAT_DDMMYYYY);
										Date fromDateReward = DateUtil.toDate(_fromDateReward, DateUtil.DATE_FORMAT_DDMMYYYY);
										header.fromApplyDate = fromDateReward;
										errRow.setContent11(_fromDateReward);
									} else if (cellFromDateReward.getCellType() == Cell.CELL_TYPE_NUMERIC && cellFromDateReward.getCellStyle() != null && DateUtil.DATE_FORMAT_VISIT.equals(cellFromDateReward.getCellStyle().getDataFormatString())) {
										if (cellFromDateReward.getDateCellValue() != null || StringUtil.isNullOrEmpty(cellFromDateReward.getStringCellValue())) {
											String _fromDateReward = DateUtil.toDateString(cellFromDateReward.getDateCellValue(), DateUtil.DATE_FORMAT_VISIT);
											Date fromDateReward = DateUtil.toDate(_fromDateReward, DateUtil.DATE_FORMAT_VISIT);
											header.fromApplyDate = fromDateReward;
											errRow.setContent11(_fromDateReward);
										}
									} else if (!StringUtil.isNullOrEmpty(cellFromDateReward.getStringCellValue())) {
										try {
											String _fromDateReward = cellFromDateReward.getStringCellValue();
											if (DateUtil.checkInvalidFormatDate(_fromDateReward)) {
												messageError += R.getResource("common.invalid.format.date", R.getResource("imp.epx.tuyen.clmn.tuNgay.traThuong"));
												messageError += "\n";
												errRow.setContent11(_fromDateReward);
											} else {
												Date fromDateReward = DateUtil.toDate(_fromDateReward, DateUtil.DATE_FORMAT_DDMMYYYY);
												header.fromApplyDate = fromDateReward;
												errRow.setContent11(_fromDateReward);
											}
										} catch (Exception e1) {
											messageError += R.getResource("common.invalid.format.date", R.getResource("imp.epx.tuyen.clmn.tuNgay.traThuong"));
											messageError += "\n";
											errRow.setContent11(cellFromDateReward.getStringCellValue());
											LogUtility.logError(e1, e1.getMessage());
										}
									}
							
									if(header.fromApplyDate != null && header.fromDate != null){
										int sub = DateUtil.compareDateWithoutTime(header.fromDate,header.fromApplyDate);
										if(DateUtil.compareDateWithoutTime(header.fromDate,header.fromApplyDate) > 0){
											messageError += R.getResource("common.fromdate.reward.greater.fromdate")+"\n";
										}
									}
								}
							}

						}else if(cellFromDateReward != null){
							String _fromDateReward = getCellValueToString(cellFromDateReward); 
							errRow.setContent11(_fromDateReward);
						}
					} catch (Exception e) {
						messageError += R.getResource("catalog.promotion.import.cant.read.cell.date", iRun, "FromDateReward");
						LogUtility.logError(e, e.getMessage());
					}

					// 11 Đến ngày trả thưởng
					try {
						Cell cellToDateReward = myRow.getCell(11);
						if (Constant.VOUCHER.equals(header.rewardType+"")) {
							if (cellToDateReward != null && StringUtil.isNullOrEmpty(messageError)) {
								if (cellToDateReward.getCellType() == Cell.CELL_TYPE_NUMERIC && cellToDateReward.getCellStyle() != null && DateUtil.HSSF_DATE_FORMAT_M_D_YY.equals(cellToDateReward.getCellStyle().getDataFormatString())) {
									if (cellToDateReward.getDateCellValue() != null || !StringUtil.isNullOrEmpty(cellToDateReward.getStringCellValue())) {
										String _toDateReward = DateUtil.toDateString(cellToDateReward.getDateCellValue(), DateUtil.DATE_FORMAT_DDMMYYYY);
										Date toDateReward = DateUtil.toDate(_toDateReward, DateUtil.DATE_FORMAT_DDMMYYYY);
										header.toApplyDate = toDateReward;
										errRow.setContent12(_toDateReward);
									} else if (cellToDateReward.getCellType() == Cell.CELL_TYPE_NUMERIC && cellToDateReward.getCellStyle() != null && DateUtil.DATE_FORMAT_VISIT.equals(cellToDateReward.getCellStyle().getDataFormatString())) {
										if (cellToDateReward.getDateCellValue() != null || StringUtil.isNullOrEmpty(cellToDateReward.getStringCellValue())) {
											String _toDateReward = DateUtil.toDateString(cellToDateReward.getDateCellValue(), DateUtil.DATE_FORMAT_VISIT);
											Date toDateReward = DateUtil.toDate(_toDateReward, DateUtil.DATE_FORMAT_VISIT);
											header.toApplyDate = toDateReward;
											errRow.setContent12(_toDateReward);
										}
									} else if (!StringUtil.isNullOrEmpty(cellToDateReward.getStringCellValue())) {
										try {
											String _todateReward = cellToDateReward.getStringCellValue();
											if (DateUtil.checkInvalidFormatDate(_todateReward)) {
												messageError += R.getResource("common.invalid.format.date", R.getResource("imp.epx.tuyen.clmn.denNgay.traThuong "));
												messageError += "\n";
												errRow.setContent12(_todateReward);
											} else {
												Date toDate = DateUtil.toDate(_todateReward, DateUtil.DATE_FORMAT_DDMMYYYY);
												header.toApplyDate = toDate;
												errRow.setContent12(_todateReward);
											}
										} catch (Exception e1) {
											messageError += R.getResource("common.invalid.format.date", R.getResource("imp.epx.tuyen.clmn.denNgay.traThuong"));
											messageError += "\n";
											errRow.setContent12(cellToDateReward.getStringCellValue());
											LogUtility.logError(e1, e1.getMessage());
										}
									}
									if (header.fromApplyDate != null && header.toApplyDate != null) {
										if (DateUtil.compareDateWithoutTime(header.fromApplyDate, header.toApplyDate) > 0) {
											messageError += R.getResource("common.fromdate.greater.todate.reward") + "\n";
										}
									}
								}

							}
						}else if(cellToDateReward != null){
							String _todateReward = getCellValueToString(cellToDateReward); 
							errRow.setContent12(_todateReward);
						}
					} catch (Exception e) {
						messageError += R.getResource("catalog.promotion.import.cant.read.cell.date", iRun, "ToDateReward");
						LogUtility.logError(e, e.getMessage());
					}*/
					// 11 Loại trả thưởng
					try {
							Cell cellRewardType = myRow.getCell(11);
							ApParam apParam = apParamMgr.getApParamByCode("LIST_ALLOW_REWARD_TYPE", ApParamType.LIST_ALLOW_REWARD_TYPE);
							String strListAllowReward = apParam.getApParamName();
							strListAllowReward = strListAllowReward.replace(" ", "");
							String[] listAllowReward = strListAllowReward.split(",");
							if (cellRewardType != null && StringUtil.isNullOrEmpty(messageError)) {
								String strRewardType = getCellValueToString(cellRewardType);
								if (strRewardType != null 
									&& (Constant.DISCOUNT_MONEY.equals(strRewardType) || Constant.VOUCHER.equals(strRewardType)) 
									&& Arrays.asList(listAllowReward).contains(header.type)) 
								{
									if(Constant.DISCOUNT_MONEY.equals(strRewardType) || Constant.VOUCHER.equals(strRewardType)) 
									{
									header.rewardType = Integer.parseInt(strRewardType.trim());
									}
								}
								errRow.setContent12(strRewardType);
							}
							else
							{
								String strRewardType = getCellValueToString(cellRewardType);
								errRow.setContent12(strRewardType);
							}
						} catch (Exception e) {
							messageError += R.getResource("catalog.promotion.import.cant.read.cell.number", iRun, "RewardType");
							LogUtility.logError(e, e.getMessage());
						}
					
					// 12 Từ ngày trả thưởng
					try {
						Cell cellFromDateReward = myRow.getCell(12);
						if (Constant.VOUCHER.equals(header.rewardType+"")) {
							if (cellFromDateReward != null && StringUtil.isNullOrEmpty(messageError)) {
								if (StringUtil.isNullOrEmpty(cellFromDateReward.getStringCellValue()))
								{
									messageError += R.getResource("catalog.promotion.import.column.null", R.getResource("imp.epx.tuyen.clmn.tuNgay.traThuong"));
									messageError += "\n";
								}
								else
								if (cellFromDateReward.getCellType() == Cell.CELL_TYPE_NUMERIC && cellFromDateReward.getCellStyle() != null && DateUtil.HSSF_DATE_FORMAT_M_D_YY.equals(cellFromDateReward.getCellStyle().getDataFormatString())) {
									if (cellFromDateReward.getDateCellValue() != null || !StringUtil.isNullOrEmpty(cellFromDateReward.getStringCellValue())) {
										String _fromDateReward = DateUtil.toDateString(cellFromDateReward.getDateCellValue(), DateUtil.DATE_FORMAT_DDMMYYYY);
										Date fromDateReward = DateUtil.toDate(_fromDateReward, DateUtil.DATE_FORMAT_DDMMYYYY);
										header.fromApplyDate = fromDateReward;
										errRow.setContent13(_fromDateReward);
										
									} 
								}
									else if (cellFromDateReward.getCellType() == Cell.CELL_TYPE_NUMERIC && cellFromDateReward.getCellStyle() != null && DateUtil.DATE_FORMAT_VISIT.equals(cellFromDateReward.getCellStyle().getDataFormatString())) {
										if (cellFromDateReward.getDateCellValue() != null || StringUtil.isNullOrEmpty(cellFromDateReward.getStringCellValue())) {
											String _fromDateReward = DateUtil.toDateString(cellFromDateReward.getDateCellValue(), DateUtil.DATE_FORMAT_VISIT);
											Date fromDateReward = DateUtil.toDate(_fromDateReward, DateUtil.DATE_FORMAT_VISIT);
											header.fromApplyDate = fromDateReward;
											errRow.setContent13(_fromDateReward);
										}
									} else if (!StringUtil.isNullOrEmpty(cellFromDateReward.getStringCellValue())) {
										try {
											String _fromDateReward = cellFromDateReward.getStringCellValue();
											if (DateUtil.checkInvalidFormatDate(_fromDateReward)) {
												messageError += R.getResource("common.invalid.format.date", R.getResource("imp.epx.tuyen.clmn.tuNgay.traThuong"));
												messageError += "\n";
												errRow.setContent13(_fromDateReward);
											} else {
												Date fromDateReward = DateUtil.toDate(_fromDateReward, DateUtil.DATE_FORMAT_DDMMYYYY);
												header.fromApplyDate = fromDateReward;
												errRow.setContent13(_fromDateReward);
											}
										} catch (Exception e1) {
											messageError += R.getResource("common.invalid.format.date", R.getResource("imp.epx.tuyen.clmn.tuNgay.traThuong"));
											messageError += "\n";
											errRow.setContent13(cellFromDateReward.getStringCellValue());
											LogUtility.logError(e1, e1.getMessage());
										}
									}
									
							
									if(header.fromApplyDate != null && header.fromDate != null){
//										int sub = DateUtil.compareDateWithoutTime(header.fromDate,header.fromApplyDate);
										if(DateUtil.compareDateWithoutTime(header.fromDate,header.fromApplyDate) > 0){
											messageError += R.getResource("common.fromdate.reward.greater.fromdate")+"\n";
										}
									}
								}
						}else if(cellFromDateReward != null){
							String _fromDateReward = getCellValueToString(cellFromDateReward); 
							errRow.setContent13(_fromDateReward);
						}
					} catch (Exception e) {
						messageError += R.getResource("catalog.promotion.import.cant.read.cell.date", iRun, "FromDateReward");
						LogUtility.logError(e, e.getMessage());
					}
					
					// 11 Đến ngày trả thưởng
					try {
						Cell cellToDateReward = myRow.getCell(13);
						if (Constant.VOUCHER.equals(header.rewardType+"")) {
							if (cellToDateReward != null && StringUtil.isNullOrEmpty(messageError)) {
								if (cellToDateReward.getCellType() == Cell.CELL_TYPE_NUMERIC && cellToDateReward.getCellStyle() != null && DateUtil.HSSF_DATE_FORMAT_M_D_YY.equals(cellToDateReward.getCellStyle().getDataFormatString())) {
									if (cellToDateReward.getDateCellValue() != null || !StringUtil.isNullOrEmpty(cellToDateReward.getStringCellValue())) {
										String _toDateReward = DateUtil.toDateString(cellToDateReward.getDateCellValue(), DateUtil.DATE_FORMAT_DDMMYYYY);
										Date toDateReward = DateUtil.toDate(_toDateReward, DateUtil.DATE_FORMAT_DDMMYYYY);
										header.toApplyDate = toDateReward;
										errRow.setContent14(_toDateReward);
										}
									} else if (cellToDateReward.getCellType() == Cell.CELL_TYPE_NUMERIC && cellToDateReward.getCellStyle() != null && DateUtil.DATE_FORMAT_VISIT.equals(cellToDateReward.getCellStyle().getDataFormatString())) {
										if (cellToDateReward.getDateCellValue() != null || StringUtil.isNullOrEmpty(cellToDateReward.getStringCellValue())) {
											String _toDateReward = DateUtil.toDateString(cellToDateReward.getDateCellValue(), DateUtil.DATE_FORMAT_VISIT);
											Date toDateReward = DateUtil.toDate(_toDateReward, DateUtil.DATE_FORMAT_VISIT);
											header.toApplyDate = toDateReward;
											errRow.setContent14(_toDateReward);
										}
									} else if (!StringUtil.isNullOrEmpty(cellToDateReward.getStringCellValue())) {
										try {
											String _todateReward = cellToDateReward.getStringCellValue();
											if (DateUtil.checkInvalidFormatDate(_todateReward)) {
												messageError += R.getResource("common.invalid.format.date", R.getResource("imp.epx.tuyen.clmn.denNgay.traThuong "));
												messageError += "\n";
												errRow.setContent14(_todateReward);
											} else {
												Date toDate = DateUtil.toDate(_todateReward, DateUtil.DATE_FORMAT_DDMMYYYY);
												header.toApplyDate = toDate;
												errRow.setContent14(_todateReward);
											}
										} catch (Exception e1) {
											messageError += R.getResource("common.invalid.format.date", R.getResource("imp.epx.tuyen.clmn.denNgay.traThuong"));
											messageError += "\n";
											errRow.setContent14(cellToDateReward.getStringCellValue());
											LogUtility.logError(e1, e1.getMessage());
										}
									}
									if (header.fromApplyDate != null && header.toApplyDate != null) {
										if (DateUtil.compareDateWithoutTime(header.fromApplyDate, header.toApplyDate) > 0) {
											messageError += R.getResource("common.fromdate.greater.todate.reward") + "\n";
										}
									}
								}
						}else if(cellToDateReward != null){
							String _todateReward = getCellValueToString(cellToDateReward); 
							errRow.setContent14(_todateReward);
						}
					} catch (Exception e) {
						messageError += R.getResource("catalog.promotion.import.cant.read.cell.date", iRun, "ToDateReward");
						LogUtility.logError(e, e.getMessage());
					}
					
					if (StringUtil.isNullOrEmpty(messageError)) {
						listHeader.add(header);
						mapHeader.put(header.promotionCode, header);
					} else {
						errRow.setErrMsg(messageError);
						lstHeaderError.add(errRow);
						if (mapErrorPromotion.get(header.promotionCode) == null) {
							mapErrorPromotion.put(header.promotionCode, messageError);
						}
					}
					iRun++;
				}
			}
			//Sheet Don vi tham gia
			if (unitSheet != null) {
				Iterator<?> rowIter = unitSheet.rowIterator();
				iRun = 0;
				int maxSizeSheet3 = 5;
				List<Shop> listShopChild;
				while (rowIter.hasNext()) {
					Row myRow = (Row) rowIter.next();
					if (iRun == 0) {
						iRun++;
						continue;
					}
					boolean isContinue = true;
					for (int i = 0; i < maxSizeSheet3; i++) {
						if (myRow.getCell(i) != null) {
							isContinue = false;
							break;
						}
					}
					if (isContinue) {
						continue;
					}
					ExcelPromotionUnit unitPromo = new ExcelPromotionUnit();
					CellBean errRow = new CellBean();
					String messageError = "";
					Boolean isHasData = false;
					try {
						String promotionCode = getCellValueToString(myRow.getCell(0));
						String shopCode = getCellValueToString(myRow.getCell(1));
						String quality = getCellValueToString(myRow.getCell(2));
						String totalAmount = getCellValueToString(myRow.getCell(3));
						String amount = getCellValueToString(myRow.getCell(4));
						
						if (!StringUtil.isNullOrEmpty(promotionCode) || !StringUtil.isNullOrEmpty(shopCode) || !StringUtil.isNullOrEmpty(quality)
								|| !StringUtil.isNullOrEmpty(totalAmount) || !StringUtil.isNullOrEmpty(amount)) {
							isHasData = true;
						}
					} catch (Exception e) {
						LogUtility.logError(e, e.getMessage());
					}
					// 1 Mã CTKM
					try {
						Cell cellPromotionCode = myRow.getCell(0);
						if (cellPromotionCode != null && StringUtil.isNullOrEmpty(messageError)) {
							if(isHasData){
								String promoCode = getCellValueToString(cellPromotionCode);
								PromotionProgram newPromotionProgram;
								if (StringUtil.isNullOrEmpty(promoCode)) {
									messageError += R.getResource("catalog.promotion.import.promotion.code.obligate") + "\n";
								} else {
									newPromotionProgram = promotionProgramMgr.getPromotionProgramByCode(promoCode);
									if(newPromotionProgram == null 
											&& mapHeader.get(promoCode) == null){
										messageError += R.getResource("catalog.promotion.import.not.init") + "\n";
									} else {
										unitPromo.promotionCode = promoCode;
									}
								}
								errRow.setContent1(promoCode);
							}
						}
					} catch (Exception e) {
						messageError += Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "catalog.promotion.import.cant.read.cell.string", Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "Mã CTKM"));
						messageError += "\n";
						LogUtility.logError(e, e.getMessage());
					}
					// 2 Mã đơn vị
					try {
						Cell cellUnitCode = myRow.getCell(1);
						if (cellUnitCode != null && StringUtil.isNullOrEmpty(messageError)) {
							if(isHasData){
								String unitCode = getCellValueToString(cellUnitCode);
								// Kiem tra ma don vi empty
								if(StringUtil.isNullOrEmpty(unitCode)){
									messageError += R.getResource("catalog.promotion.import.unit.code.obligate") + "\n";
								} else {
									// Kiem tra don vi ton tai trong he thong
									if(shopMgr.getShopByCode(unitCode) == null){
										messageError += R.getResource("catalog.promotion.import.unit.code.not.permission") + "\n";
									} else if (currentUser != null && currentUser.getShopRoot() != null){ // kiem tra don vi co thuoc quyen quan ly cua user
										listShopChild = promotionProgramMgr.getListChildByShopId(currentUser.getShopRoot().getShopId());
										// Kiem tra shop co thuoc quen quan ly cua user dang nhap
										boolean isShopMapWithUser = false;
										for(Shop shop: listShopChild){
											 if(unitCode.toLowerCase().equals(shop.getShopCode().toLowerCase())){
												 isShopMapWithUser = true;
												 break;
											}
										}
										if(!isShopMapWithUser){
											messageError += R.getResource("catalog.promotion.import.unit.code.not.permission.by.current.user") + "\n";
										} else {
											unitPromo.unitCode = unitCode;
										}
									}
								}
								errRow.setContent2(unitCode);
							}
						}
						
					} catch (Exception e) {
						messageError += Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "catalog.promotion.import.cant.read.cell.string", Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "Mã CTKM"));
						messageError += "\n";
						LogUtility.logError(e, e.getMessage());
					}
					// 3 Số suất 
					try {
						Cell cellQuantityMax = myRow.getCell(2);
						if (cellQuantityMax != null && StringUtil.isNullOrEmpty(messageError)) {
							String quantityMax = getCellValueToString(cellQuantityMax);
							if(quantityMax != null){
								quantityMax = quantityMax.trim().replace(",", "");
							}
							if (quantityMax != null && quantityMax.trim().length() <= 9 
									&& StringUtil.isFloat(quantityMax) && Double.parseDouble(quantityMax.trim()) > 0
									&& quantityMax.contains(".") == false) {
								unitPromo.quantityMax = Integer.parseInt(quantityMax.trim().replace(",", ""));
							} else if(quantityMax != null && quantityMax.length() > 9 ){
								messageError += R.getResource("catalog.promotion.import.over.max.length") + "\n";
								messageError = messageError.replaceAll("%max%", "9");
								messageError = messageError.replaceAll("%colName%", "Số suất");
							}
							else if (StringUtil.isNullOrEmpty(quantityMax) == false) {
								messageError += R.getResource("catalog.promotion.import.quantity.max.incorrect.format") + "\n";
							}
							errRow.setContent3(quantityMax);
						}else if(cellQuantityMax != null){
							// String quantityMax = getCellValueToString(cellQuantityMax);
						}
					} catch (Exception e) {
						messageError += Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "catalog.promotion.import.cant.read.cell.number", Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "Số suất"));
						messageError += "\n";
						LogUtility.logError(e, e.getMessage());
					}
					// 4 Số tiền amountMax
					try {
						Cell cellAmountMax = myRow.getCell(3);
						if (cellAmountMax != null && StringUtil.isNullOrEmpty(messageError)) {
							String amountMax = getCellValueToString(cellAmountMax);
							if(amountMax != null){
								amountMax = amountMax.trim().replace(",", "");
							}
							if (amountMax != null && amountMax.length() <= 9 && StringUtil.isFloat(amountMax) 
									&& Double.parseDouble(amountMax.trim()) > 0 && amountMax.contains(".") == false) {
								unitPromo.amountMax = new BigDecimal(amountMax.trim().replace(",", ""));
							} else if(amountMax != null && amountMax.length() > 9){
								messageError += R.getResource("catalog.promotion.import.over.max.length") + "\n";
								messageError = messageError.replaceAll("%max%", "9");
								messageError = messageError.replaceAll("%colName%", "Số tiền");
							}
							else if (StringUtil.isNullOrEmpty(amountMax) == false) {
								messageError += R.getResource("catalog.promotion.import.amount.max.incorrect.format") + "\n";
							}
							errRow.setContent4(amountMax);
						}else if(cellAmountMax != null){
							String amountMax = getCellValueToString(cellAmountMax);
							errRow.setContent4(amountMax);
						}

					} catch (Exception e) {
						messageError += Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "catalog.promotion.import.cant.read.cell.number", Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "Số tiền"));
						messageError += "\n";
						LogUtility.logError(e, e.getMessage());
					}
					// 5 Số lượng numMax
					try {
						Cell cellNumMax = myRow.getCell(4);
						if (cellNumMax != null && StringUtil.isNullOrEmpty(messageError)) {
							String numMax = getCellValueToString(cellNumMax);
							numMax = numMax.trim().replace(",", "");
							if (numMax != null && numMax.length() <= 9 
									&& StringUtil.isFloat(numMax) 
									&& Double.parseDouble(numMax.trim()) > 0
									&& numMax.contains(".") == false) {
								unitPromo.numMax = new BigDecimal(numMax.trim().replace(",", ""));
							} else if(numMax != null && numMax.length() > 9){
								messageError += R.getResource("catalog.promotion.import.over.max.length") + "\n";
								messageError = messageError.replaceAll("%max%", "9");
								messageError = messageError.replaceAll("%colName%", "Số lượng");
							}else if (StringUtil.isNullOrEmpty(numMax) == false) {							
								messageError += R.getResource("catalog.promotion.import.num.max.incorrect.format") + "\n";
							}
							errRow.setContent5(numMax);
						}else if(cellNumMax != null){
							String numMax = getCellValueToString(cellNumMax);
							errRow.setContent5(numMax);
						}
					} catch (Exception e) {
						messageError += Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "catalog.promotion.import.cant.read.cell.number",
								Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "Số lượng"));
						messageError += "\n";
						LogUtility.logError(e, e.getMessage());
					}
					if (StringUtil.isNullOrEmpty(messageError)) {
						listUnit.add(unitPromo);
						mapUnit.put(unitPromo.promotionCode, unitPromo);
					}else{
						errRow.setContent6(messageError);
						listUnitError.add(errRow);
//						if(mapErrorUnit.get(unitPromo.promotionCode) == null){
//							mapErrorUnit.put(unitPromo.promotionCode, messageError);
//						}
					}
					/*if (StringUtil.isNullOrEmpty(messageError)) {
						listHeader.add(header);
						mapHeader.put(header.promotionCode, header);
					} else {
						errRow.setContent13(messageError);
						lstHeaderError.add(errRow);
						if (mapErrorPromotion.get(header.promotionCode) == null) {
							mapErrorPromotion.put(header.promotionCode, messageError);
						}
					}*/

					iRun++;
				}
			}

			//Sheet Co cau KM
			/////
			if (detailSheet != null) {	
				Iterator<?> rowIter = detailSheet.rowIterator();
				String previousPromotionCode = null;
				int typeKM;
				Map<String, Integer> mapPromotionType = new HashMap<String, Integer>();
				iRun = 0;
				Map<String, String[]> mapArrayProduct = new HashMap<String, String[]>();
				Map<String, BigDecimal[]> mapArraySaleQuantity = new HashMap<String, BigDecimal[]>();
				Map<String, BigDecimal[]> mapArraySaleAmount = new HashMap<String, BigDecimal[]>();
				Map<String, BigDecimal[]> mapArrayDiscountAmount = new HashMap<String, BigDecimal[]>();
				Map<String, Float[]> mapArrayDiscountPercent = new HashMap<String, Float[]>();
				Map<String, Integer[]> mapArrayQuantityUnit = new HashMap<String, Integer[]>();
				Map<String, String[]> mapArrayFreeProduct = new HashMap<String, String[]>();
				Map<String, BigDecimal[]> mapArrayFreeQuantity = new HashMap<String, BigDecimal[]>();
				Map<String, Integer[]> mapArrayFreeQuantityUnit = new HashMap<String, Integer[]>();
				Map<String, Boolean[]> mapArrayAndOr = new HashMap<String, Boolean[]>();
				Map<String, String[]> mapPromoGroupCode = new HashMap<String, String[]>();
				Map<String, String[]> mapPromoGroupName = new HashMap<String, String[]>();
				Map<String, String[]> mapPromoLevelCode = new HashMap<String, String[]>();
//				Map<String, Integer[]> mapMultiple = new HashMap<String, Integer[]>();
//				Map<String, Integer[]> mapRecursive = new HashMap<String, Integer[]>();
//				Map<String, Integer[]> mapDkgh = new HashMap<String,Integer[]>();
				
				LinkedHashMap<String, Integer> lstProductPromo = new LinkedHashMap<String, Integer>();
				Map<String, List<Row>> lstRow = new HashMap<String, List<Row>>();
				int indexProductPromo = 0;
				int maxSizeSheet2 = 12;
				PromotionProgram existPromotion = null;
				while (rowIter.hasNext()) {
					Row myRow = (Row) rowIter.next();
					if (iRun == 0) {
						iRun++;
						continue;
					}
					boolean isContinue = true;
					//Kiem tra su hop le cua Row Import
					for (int i = 0; i < maxSizeSheet2; i++) {
						if (myRow.getCell(i) != null) {
							isContinue = false;
							break;
						}
					}
					if (isContinue) {
						continue;
					}
					ExcelPromotionDetail detail = new ExcelPromotionDetail();
					CellBean errRow = new CellBean();
					String messageError = "";
					//0 get promotionCode
					String promotionCode = null;
					try {
						Cell cellPromotionCode = myRow.getCell(0);
						if (cellPromotionCode != null && StringUtil.isNullOrEmpty(messageError)) {
							//						try {
							//							promotionCode = cellPromotionCode.getStringCellValue();
							//						} catch (Exception e) {
							//							promotionCode = String.valueOf(cellPromotionCode.getNumericCellValue());
							//							LogUtility.logError(e, e.getMessage());
							//						}
							promotionCode = getCellValueToString(cellPromotionCode);
							promotionCode = promotionCode != null ? promotionCode.trim().toUpperCase().trim() : "";
							if (StringUtil.isNullOrEmpty(promotionCode)) {
								continue;
							} else {
								messageError += ValidateUtil.validateField(promotionCode, "catalog.promotion.import.column.progcode", 50, ConstantManager.ERR_REQUIRE, ConstantManager.ERR_MAX_LENGTH, ConstantManager.ERR_EXIST_SPECIAL_CHAR_IN_CODE);
							}
							existPromotion = promotionProgramMgr.getPromotionProgramByCode(promotionCode);
							if (existPromotion == null && mapHeader.get(promotionCode) == null) {
								messageError += R.getResource("catalog.promotion.import.not.init") + "\n";
								//messageError += Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "catalog.promotion.program.not.exists");
								//messageError += "\n";
								//else if(existPromotion != null && mapHeader.get(promotionCode) == null){
							} else if(existPromotion != null && ActiveType.RUNNING.equals(existPromotion.getStatus())){
								messageError += R.getResource("catalog.promotion.program.exists") + "\n";
							}
							detail.promotionCode = promotionCode;
							errRow.setContent1(promotionCode);
							if (StringUtil.isNullOrEmpty(promotionCode)) {
								messageError += Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "common.required", Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "catalog.promotion.code"));
								messageError += "\n";
							}
							
						} else {
							continue;
						}
					} catch (Exception e) {
						messageError += Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "catalog.promotion.import.cant.read.cell.string", iRun, "Mã CTKM") + "\n";
						LogUtility.logError(e, e.getMessage());
					}
					//1 get type
					try {
						if (myRow.getCell(1) != null && StringUtil.isNullOrEmpty(messageError)) {
							//						try {
							//							detail.type = myRow.getCell(1).getStringCellValue();
							//						} catch (Exception e) {
							//							detail.type = String.valueOf(myRow.getCell(1).getNumericCellValue());
							//							LogUtility.logError(e, e.getMessage());
							//						}
							detail.type = getCellValueToString(myRow.getCell(1));
							detail.type = detail.type != null ? detail.type.trim().toUpperCase().trim() : "";
							if (StringUtil.isNullOrEmpty(detail.type)) {
								continue;
							} else {
								if (mapCheckType.get(detail.type) == null) {
									messageError += Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "catalog.exists.before", Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "catalog.promotion.type"));
									messageError += "\n";
								} else if (mapType.get(promotionCode) == null) {
									if (null != existPromotion && existPromotion.getType().equalsIgnoreCase(detail.type)) {
										mapType.put(detail.promotionCode, detail.type);
									} else {
										messageError += Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "catalog.promotion.import.type.is.not.same2") + "\n";
									}
								} else if (!mapType.get(promotionCode).equals(detail.type)) {
									messageError += Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "catalog.promotion.import.type.is.not.same2") + "\n";
								}
							}
							errRow.setContent2(getCellValueToString(myRow.getCell(1)));
						}else if(myRow.getCell(1) != null){
							errRow.setContent2(getCellValueToString(myRow.getCell(1)));
						}
					} catch (Exception e) {
						messageError += Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "catalog.promotion.import.cant.read.cell.string", iRun, "Loại CTKM") + "\n";
						LogUtility.logError(e, e.getMessage());
					}
					//2 Mã nhóm
					/*try {
						Cell cellGroupCode = myRow.getCell(2);
						String groupCode = null;
						if (cellGroupCode != null && StringUtil.isNullOrEmpty(messageError)) {
							groupCode = getCellValueToString(cellGroupCode);
							messageError = ValidateUtil.validateField(noticeCode, "catalog.promotion.noticecode", 100, ConstantManager.ERR_EXIST_SPECIAL_CHAR_IN_SPECIAL);
							if (StringUtil.isNullOrEmpty(groupCode)) {
								messageError += Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "catalog.promotion.import.promotion.group.code.obligate") + "\n";
							} else {
								detail.promoGroupCode = groupCode;
							}
							errRow.setContent3(groupCode);
						}else if (cellGroupCode != null) {
							groupCode = getCellValueToString(cellGroupCode);
							errRow.setContent3(groupCode);
						}else if (cellGroupCode == null) {
							messageError += Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "catalog.promotion.import.promotion.group.code.obligate") + "\n";
						}
					} catch (Exception e) {
						messageError += Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "catalog.promotion.import.cant.read.cell.string", iRun, "Mã nhóm") + "\n";
						LogUtility.logError(e, e.getMessage());
					}
					// 3 Tên nhóm
					try {
						Cell cellGroupName = myRow.getCell(3);
						String groupName = null;
						if (cellGroupName != null && StringUtil.isNullOrEmpty(messageError)) {
							groupName = getCellValueToString(cellGroupName);
							if (StringUtil.isNullOrEmpty(groupName)) {
								messageError += Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "catalog.promotion.import.promotion.group.name.obligate") + "\n";
							} else {
								detail.promoGroupName = groupName;
							}
							errRow.setContent4(groupName);
						}else if (cellGroupName != null) {
							groupName = getCellValueToString(cellGroupName);
							errRow.setContent4(groupName);
						}else if (cellGroupName == null) {
							messageError += Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "catalog.promotion.import.promotion.group.name.obligate") + "\n";
						}
					} catch (Exception e) {
						messageError += Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "catalog.promotion.import.cant.read.cell.string", iRun, "Tên nhóm") + "\n";
						LogUtility.logError(e, e.getMessage());
					}
					// 4 Mã mức
					try {
						Cell cellLevelCode = myRow.getCell(4);
						String levelCode = null;
						if (cellLevelCode != null && StringUtil.isNullOrEmpty(messageError)) {
							levelCode = getCellValueToString(cellLevelCode);
							if (StringUtil.isNullOrEmpty(levelCode)) {
								messageError += Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "catalog.promotion.import.promotion.level.code.obligate") + "\n";
							} else {
								detail.promoLevelCode = levelCode;
							}
							errRow.setContent5(levelCode);
						} else if (cellLevelCode != null) {
							levelCode = getCellValueToString(cellLevelCode);
							errRow.setContent5(levelCode);
						}else if (cellLevelCode == null) {
							messageError += Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "catalog.promotion.import.promotion.level.code.obligate") + "\n";
						}
					} catch (Exception e) {
						messageError += Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "catalog.promotion.import.cant.read.cell.string", iRun, "Tên nhóm") + "\n";
						LogUtility.logError(e, e.getMessage());
					}*/
					//5 Tên mức
					/*try {
						Cell cellLevelName = myRow.getCell(5);
						String levelName = null;
						if (cellLevelName != null && StringUtil.isNullOrEmpty(messageError)) {
							levelName = getCellValueToString(cellLevelName);
							if (StringUtil.isNullOrEmpty(levelName)) {
								messageError += Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "catalog.promotion.import.promotion.level.name.obligate ") + "\n";
							} else {
								detail.promoLevelName = levelName;
							}
							errRow.setContent6(levelName);
						} else if (cellLevelName == null) {
							messageError += Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "catalog.promotion.import.promotion.level.name.obligate ") + "\n";
						}
					} catch (Exception e) {
						messageError += Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "catalog.promotion.import.cant.read.cell.string", iRun, "Tên nhóm") + "\n";
						LogUtility.logError(e, e.getMessage());
					}*/
					//2 get productCode
					String productCode = "";
					try {
						if (checkColumnNecessary(detail.type, 2) && StringUtil.isNullOrEmpty(messageError)) {
							Cell cellProductCode = myRow.getCell(2);
							if (cellProductCode != null) {
								//							try {
								//								productCode = cellProductCode.getStringCellValue();
								//							} catch (Exception ex) {
								//								productCode = String.valueOf(cellProductCode.getNumericCellValue());
								//								LogUtility.logError(ex, ex.getMessage());
								//							}
								productCode = getCellValueToString(cellProductCode);
								if (!StringUtil.isNullOrEmpty(productCode)) {
									Product product = productMgr.getProductByCode(productCode.trim());
									if (product == null) {
										messageError += Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "common.not.exist.in.db", Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "catalog.buyproduct.code"));
										messageError += "\n";
									}
									detail.productCode = productCode.toUpperCase().trim();
								} else {
									messageError += R.getResource("catalog.promotion.import.column.null", "Mã Sản Phẩm Mua");
								}
							} else {
								messageError += R.getResource("catalog.promotion.import.column.null", "Mã Sản Phẩm Mua");
							}
						}
						if (myRow.getCell(2) != null) {
							errRow.setContent3(getCellValueToString(myRow.getCell(2)));
						}
					} catch (Exception e) {
						messageError += R.getResource("catalog.promotion.import.get.product.error", productCode);
						LogUtility.logError(e, e.getMessage());
					}
					//3 getQuantity
					try {
						if (checkColumnNecessary(detail.type, 3) && StringUtil.isNullOrEmpty(messageError)) {
							Cell cellQuantity = myRow.getCell(3);
							if (cellQuantity != null && cellQuantity.getCellType() != Cell.CELL_TYPE_BLANK) {
								if (cellQuantity.getCellType() == Cell.CELL_TYPE_NUMERIC) {
									BigDecimal quantity = new BigDecimal(cellQuantity.getNumericCellValue());
									if (quantity == null || quantity.compareTo(BigDecimal.ZERO) <= 0) {
										messageError += Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "common.not.date", "SL Sản Phẩm Mua");
									}
									detail.saleQuantity = quantity;
								} else {
									messageError += R.getResource("catalog.promotion.import.column.invalid.format.number", "SL Sản Phẩm Mua");
								}
							} else {
								messageError += R.getResource("catalog.promotion.import.column.null", "SL Sản Phẩm Mua");
							}
						}
						if (myRow.getCell(3) != null) {
							errRow.setContent4(getCellValueToString(myRow.getCell(3)));
						}
					} catch (Exception e) {
						messageError += Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "catalog.promotion.import.cant.read.cell.number", iRun, "SL Sản Phẩm Mua");
						LogUtility.logError(e, e.getMessage());
					}
					//4 get UOM
					try {
						if (checkColumnNecessary(detail.type, 4) && StringUtil.isNullOrEmpty(messageError)) {
							Cell cellProductUnit = myRow.getCell(4);
							if (cellProductUnit != null && cellProductUnit.getCellType() != Cell.CELL_TYPE_BLANK) {
								if (cellProductUnit.getCellType() == Cell.CELL_TYPE_STRING) {
									String unit = cellProductUnit.getStringCellValue();
									if (unit == null || StringUtil.isNullOrEmpty(unit)) {
										messageError += Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "common.not.date", "Đơn Vị Tính Cho SP Mua");
									}
									if (unit.trim().toLowerCase().equals("LẺ".toLowerCase())) {
										detail.productUnit = 1;
									} else {
										detail.productUnit = 2;
									}
								} else {
									messageError += R.getResource("catalog.promotion.import.column.invalid.format.number", "Đơn Vị Tính Cho SP Mua");
								}
							} else {
								messageError += R.getResource("catalog.promotion.import.column.null", "Đơn Vị Tính Cho SP Mua");
							}
						}
						if (myRow.getCell(4) != null) {
							errRow.setContent5(getCellValueToString(myRow.getCell(4)));
						}
					} catch (Exception e) {
						messageError += Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "catalog.promotion.import.cant.read.cell.string", iRun, "Đơn Vị Tính Cho SP Mua");
						LogUtility.logError(e, e.getMessage());
					}
					//5 getAmount
					try {
						if (checkColumnNecessary(detail.type, 5) && StringUtil.isNullOrEmpty(messageError)) {
							Cell cellAmount = myRow.getCell(5);
							if (cellAmount != null && cellAmount.getCellType() != Cell.CELL_TYPE_BLANK) {
								if (cellAmount.getCellType() == Cell.CELL_TYPE_NUMERIC) {
									BigDecimal amount = BigDecimal.valueOf(cellAmount.getNumericCellValue());
									if (amount == null || amount.compareTo(BigDecimal.ZERO) < 0) {
										messageError += Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "common.not.date", "Số Tiền SP Mua");
									}
									detail.saleAmount = amount;
								} else {
									messageError += R.getResource("catalog.promotion.import.column.invalid.format.number", "Số Tiền SP Mua");
								}
							} else {
								messageError += R.getResource("catalog.promotion.import.column.null", "Số Tiền SP Mua");
							}
						}
						if (myRow.getCell(5) != null) {
							errRow.setContent6(getCellValueToString(myRow.getCell(5)));
						}
					} catch (Exception e) {
						messageError += Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "catalog.promotion.import.cant.read.cell.number", iRun, "Số Tiền SP Mua");
						LogUtility.logError(e, e.getMessage());
					}
					//6 getDiscount Amount
					try {
						if (checkColumnNecessary(detail.type, 6) && StringUtil.isNullOrEmpty(messageError)) {
							Cell cellDiscountAmount = myRow.getCell(6);
							if (cellDiscountAmount != null && cellDiscountAmount.getCellType() != Cell.CELL_TYPE_BLANK) {
								if (cellDiscountAmount.getCellType() == Cell.CELL_TYPE_NUMERIC) {
									BigDecimal discountAmount = BigDecimal.valueOf(cellDiscountAmount.getNumericCellValue());
									if (discountAmount == null || discountAmount.compareTo(BigDecimal.ZERO) < 0) {
										messageError += Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "common.not.date", "Số Tiền SP KM");
									}
									detail.discountAmount = discountAmount;
								} else {
									messageError += R.getResource("catalog.promotion.import.column.invalid.format.number", "Số Tiền SP KM");
								}
							} else {
								messageError += R.getResource("catalog.promotion.import.column.null", "Số Tiền SP KM");
							}
						}
						if (myRow.getCell(6) != null) {
							errRow.setContent7(getCellValueToString(myRow.getCell(6)));
						}
					} catch (Exception e) {
						messageError += Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "catalog.promotion.import.cant.read.cell.number", iRun, "Số Tiền SP KM");
						LogUtility.logError(e, e.getMessage());
					}
					//7 get discount percent
					try {
						if (checkColumnNecessary(detail.type, 7) && StringUtil.isNullOrEmpty(messageError)) {
							Cell cellDiscountPercent = myRow.getCell(7);
							if (cellDiscountPercent != null && cellDiscountPercent.getCellType() != Cell.CELL_TYPE_BLANK) {
								if (cellDiscountPercent.getCellType() == Cell.CELL_TYPE_NUMERIC) {
									Float discountPercent = (float) cellDiscountPercent.getNumericCellValue();
									if (discountPercent == null || discountPercent < 0 || discountPercent > 100) {
										messageError += Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "common.not.date", "% KM");
									}
									detail.discountPercent = discountPercent;
								} else {
									messageError += Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "catalog.promotion.import.column.invalid.format.float", "% KM");
								}
							} else {
								messageError += R.getResource("catalog.promotion.import.column.null", "% KM");
							}
						}
						if (myRow.getCell(7) != null) {
							errRow.setContent8(getCellValueToString(myRow.getCell(7)));
						}
					} catch (Exception e) {
						messageError += Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "catalog.promotion.import.cant.read.cell.number", iRun, "DiscPer");
						LogUtility.logError(e, e.getMessage());
					}
					//8 get Free product code
					try {
						if (checkColumnNecessary(detail.type, 8) && StringUtil.isNullOrEmpty(messageError)) {
							Cell cellFreeProductCode = myRow.getCell(8);
							if (cellFreeProductCode != null) {
								String freeProductCode = getCellValueToString(cellFreeProductCode);
								if (!StringUtil.isNullOrEmpty(freeProductCode)) {
									Product freeProduct = productMgr.getProductByCode(freeProductCode.trim());
									if (freeProduct == null) {
										messageError += Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "common.not.exist.in.db", Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "catalog.disproduct.code"));
										messageError += "\n";
									}
									detail.freeProductCode = freeProductCode.toUpperCase().trim();
								}
							} else {
								messageError += R.getResource("catalog.promotion.import.column.null", "Mã SP KM");
							}
						}
						if (myRow.getCell(8) != null) {
							errRow.setContent9(getCellValueToString(myRow.getCell(8)));
						}
					} catch (Exception e) {
						messageError += Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "catalog.promotion.import.cant.read.cell.string", iRun, "Mã SP KM");
						LogUtility.logError(e, e.getMessage());
					}
					//9 get free Quantity
					try {
						if (checkColumnNecessary(detail.type, 9) && StringUtil.isNullOrEmpty(messageError)) {
							Cell cellFreeQuantity = myRow.getCell(9);
							if (cellFreeQuantity != null && cellFreeQuantity.getCellType() != Cell.CELL_TYPE_BLANK) {
								if (cellFreeQuantity.getCellType() == Cell.CELL_TYPE_NUMERIC) {
									BigDecimal freeQuantity = new BigDecimal(cellFreeQuantity.getNumericCellValue());
									if (freeQuantity == null || freeQuantity.compareTo(BigDecimal.ZERO) < 0) {
										messageError += Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "common.not.date", "Số Lượng KM");
									}
									detail.freeQuantity = freeQuantity;
								} else {
									messageError += R.getResource("catalog.promotion.import.column.invalid.format.number", "Số Lượng KM");
								}
							} else {
								messageError += R.getResource("catalog.promotion.import.column.null", "Số Lượng KM");
							}
						}
						if (myRow.getCell(9) != null) {
							errRow.setContent10(getCellValueToString(myRow.getCell(9)));
						}
					} catch (Exception e) {
						messageError += Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "catalog.promotion.import.cant.read.cell.number", iRun, "Số Lượng KM");
						LogUtility.logError(e, e.getMessage());
					}
					//10 get Free UOM
					try {
						if (checkColumnNecessary(detail.type, 10) && StringUtil.isNullOrEmpty(messageError)) {
							Cell cellProductUnit = myRow.getCell(10);
							if (cellProductUnit != null && cellProductUnit.getCellType() != Cell.CELL_TYPE_BLANK) {
								if (cellProductUnit.getCellType() == Cell.CELL_TYPE_STRING) {
									String unit = cellProductUnit.getStringCellValue();
									if (unit == null || StringUtil.isNullOrEmpty(unit)) {
										messageError += Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "common.not.date", "Đơn Vị Tính cho SP KM");
									}
									if (unit.trim().toLowerCase().equals("LẺ".toLowerCase())) {
										detail.freeProductUnit = 1;
									} else {
										detail.freeProductUnit = 2;
									}
								} else {
									messageError += R.getResource("catalog.promotion.import.column.invalid.format.number", "Đơn Vị Tính cho SP KM");
								}
							}
						}
						if (myRow.getCell(10) != null) {
							errRow.setContent11(getCellValueToString(myRow.getCell(10)));
						}
					} catch (Exception e) {
						messageError += Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "catalog.promotion.import.cant.read.cell.string", iRun, "Đơn Vị Tính cho SP KM");
						LogUtility.logError(e, e.getMessage());
					}
					//11 get And Or
					try {
						if (checkColumnNecessary(detail.type, 11) && StringUtil.isNullOrEmpty(messageError)) {
							Cell cellAndOrCell = myRow.getCell(11);
							if (cellAndOrCell != null) {
								String value;
								if (cellAndOrCell.getCellType() != Cell.CELL_TYPE_NUMERIC) {
									value = cellAndOrCell.getStringCellValue();
								} else {
									value = String.valueOf((float) cellAndOrCell.getNumericCellValue());
								}
								if ("X".equals(value.trim().toUpperCase())) {
									detail.andOr = true;
									errRow.setContent12(value);
								} else if ("".equals(value.trim().toUpperCase())) {
									detail.andOr = false;
									errRow.setContent12(value);
								} else {
									errRow.setContent13(value);
									messageError += Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "catalog.promotion.import.read.cell.format.invalid", iRun, "AllFreeItemcode", "['','X']");
								}
							} else {
								detail.andOr = false;
								errRow.setContent12("");
							}
						}
					} catch (Exception e) {
						messageError += Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "catalog.promotion.import.read.cell.format.invalid", iRun, "AllFreeItemcode", "['','X']");
						LogUtility.logError(e, e.getMessage());
					}
					
					// 12 ĐKGH
					/*try{
						ApParam apParam = apParamMgr.getApParamByCode("LIST_ALLOW_DKGH", ApParamType.LIST_ALLOW_DKGH);
						String strListPromo = apParam.getApParamName();
						Cell cellDKGH = myRow.getCell(17);
						String[] ListAllow = strListPromo.split(",");
						if(Arrays.asList(ListAllow).contains(detail.type)){
							if(cellDKGH != null && StringUtil.isNullOrEmpty(messageError)){
								String dkgh = getCellValueToString(cellDKGH);
								if(dkgh != null 
										&& (Constant.HAVE_CONDITION.equals(dkgh) || (Constant.NON_CONDITION.equals(dkgh)))){
									detail.dkgh = Integer.parseInt(dkgh.trim());
								}else if(dkgh != null){
									messageError +=  R.getResource("catalog.promotion.import.dkgh.incorrect.format")+"\n";
								}
								errRow.setContent18(dkgh);
							}
						}else if(cellDKGH != null){
							String dkgh = getCellValueToString(cellDKGH);
							errRow.setContent18(dkgh);
						}
					}catch (Exception e) {
						messageError += Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "catalog.promotion.import.cant.read.cell.number", iRun, "DKGH", "['','X']");
						LogUtility.logError(e, e.getMessage());
					}*/
					if (mapPromotionTypeCheck.get(detail.promotionCode) == null) {
						mapPromotionTypeCheck.put(detail.promotionCode, detail.type);
					}
					if (!promotionCode.equals(previousPromotionCode)) {
						if (mapPromotionType.get(detail.promotionCode) != null) {//da ton tai ctkm nay truoc do roi
							//typeKM = mapPromotionType.get(detail.promotionCode);//=> lay ra loai cua no thoi
						} else {
							if (!StringUtil.isNullOrEmpty(detail.productCode) && detail.saleQuantity != null && detail.saleQuantity.compareTo(BigDecimal.ZERO) > 0 && !StringUtil.isNullOrEmpty(detail.freeProductCode) && detail.freeQuantity != null
									&& detail.freeQuantity.compareTo(BigDecimal.ZERO) > 0) {
								typeKM = 1;//ZV03
							} else if (!StringUtil.isNullOrEmpty(detail.productCode) && detail.saleQuantity != null && detail.saleQuantity.compareTo(BigDecimal.ZERO) > 0 && detail.discountAmount != null && detail.discountAmount.compareTo(
									BigDecimal.ZERO) > 0) {
								typeKM = 2;//ZV02
							} else if (!StringUtil.isNullOrEmpty(detail.productCode) && detail.saleQuantity != null && detail.saleQuantity.compareTo(BigDecimal.ZERO) > 0 && detail.discountPercent != null && detail.discountPercent > 0) {
								typeKM = 3;//ZV01
							} else if (!StringUtil.isNullOrEmpty(detail.productCode) && detail.saleAmount != null && detail.saleAmount.compareTo(BigDecimal.ZERO) > 0 && !StringUtil.isNullOrEmpty(detail.freeProductCode) && detail.freeQuantity != null
									&& detail.freeQuantity.compareTo(BigDecimal.ZERO) > 0) {
								typeKM = 4;
							} else if (!StringUtil.isNullOrEmpty(detail.productCode) && detail.saleAmount != null && detail.saleAmount.compareTo(BigDecimal.ZERO) > 0 && detail.discountAmount != null && detail.discountAmount.compareTo(
									BigDecimal.ZERO) > 0) {
								typeKM = 5;
							} else if (!StringUtil.isNullOrEmpty(detail.productCode) && detail.saleAmount != null && detail.saleAmount.compareTo(BigDecimal.ZERO) > 0 && detail.discountPercent != null && detail.discountPercent > 0) {
								typeKM = 6;
							} else if (detail.saleAmount != null && detail.saleAmount.compareTo(BigDecimal.ZERO) > 0 && !StringUtil.isNullOrEmpty(detail.freeProductCode) && detail.freeQuantity != null && detail.freeQuantity.compareTo(
									BigDecimal.ZERO) > 0) {
								typeKM = 7;
							} else if (detail.saleAmount != null && detail.saleAmount.compareTo(BigDecimal.ZERO) > 0 && detail.discountAmount != null && detail.discountAmount.compareTo(BigDecimal.ZERO) > 0) {
								typeKM = 8;
							} else if (detail.saleAmount != null && detail.saleAmount.compareTo(BigDecimal.ZERO) > 0 && detail.discountPercent != null && detail.discountPercent > 0) {
								typeKM = 9;
							} else if (detail.saleQuantity != null && detail.saleQuantity.compareTo(BigDecimal.ZERO) > 0 && !StringUtil.isNullOrEmpty(detail.freeProductCode) && detail.freeQuantity != null && detail.freeQuantity.compareTo(
									BigDecimal.ZERO) > 0) {
								typeKM = 10;//ZV24
							} else if (detail.saleQuantity != null && detail.saleQuantity.compareTo(BigDecimal.ZERO) > 0 && detail.discountAmount != null && detail.discountAmount.compareTo(BigDecimal.ZERO) > 0) {
								typeKM = 11;//ZV23
							} else if (detail.saleQuantity != null && detail.saleQuantity.compareTo(BigDecimal.ZERO) > 0 && detail.discountPercent != null && detail.discountPercent > 0) {
								typeKM = 12;//ZV22
							} else {
								typeKM = -1;
							}
							mapPromotionType.put(detail.promotionCode, typeKM);
							previousPromotionCode = detail.promotionCode;
						}
					} else {
						//typeKM = mapPromotionType.get(detail.promotionCode);
					}

					if (StringUtil.isNullOrEmpty(messageError)) {
						List<Row> lstR = lstRow.get(detail.promotionCode);
						if (lstR == null) {
							lstR = new ArrayList<Row>();
						}
						messageError = checkDuplicate(mapType.get(detail.promotionCode), lstR, myRow);
						if(StringUtil.isNullOrEmpty(messageError)){
							listDetail.add(detail);
						}
						lstR.add(myRow);
						lstRow.put(detail.promotionCode, lstR);
					}
					if (StringUtil.isNullOrEmpty(messageError)) {
						if (mapArrayProduct.get(detail.promotionCode) == null) {
							String[] arrProduct = new String[MAX_ARRAY];
							arrProduct[iRun] = detail.productCode;
							mapArrayProduct.put(detail.promotionCode, arrProduct);
						} else {
							String[] arrProduct = mapArrayProduct.get(detail.promotionCode);
							arrProduct[iRun] = detail.productCode;
						}
						if (mapArraySaleQuantity.get(detail.promotionCode) == null) {
							BigDecimal[] arrSaleQuantity = new BigDecimal[MAX_ARRAY];
							arrSaleQuantity[iRun] = detail.saleQuantity;
							mapArraySaleQuantity.put(detail.promotionCode, arrSaleQuantity);
						} else {
							BigDecimal[] arrSaleQuantity = mapArraySaleQuantity.get(detail.promotionCode);
							arrSaleQuantity[iRun] = detail.saleQuantity;
						}
						if (mapArrayQuantityUnit.get(detail.promotionCode) == null) {
							Integer[] arrUnit = new Integer[MAX_ARRAY];
							arrUnit[iRun] = detail.productUnit;
							mapArrayQuantityUnit.put(detail.promotionCode, arrUnit);
						} else {
							Integer[] arrUnit = mapArrayQuantityUnit.get(detail.promotionCode);
							arrUnit[iRun] = detail.productUnit;
						}
						if (mapArraySaleAmount.get(detail.promotionCode) == null) {
							BigDecimal[] arrSaleAmount = new BigDecimal[MAX_ARRAY];
							arrSaleAmount[iRun] = detail.saleAmount;
							mapArraySaleAmount.put(detail.promotionCode, arrSaleAmount);
						} else {
							BigDecimal[] arrSaleAmount = mapArraySaleAmount.get(detail.promotionCode);
							arrSaleAmount[iRun] = detail.saleAmount;
						}
						if (mapArrayFreeProduct.get(detail.promotionCode) == null) {
							String[] arrFreeProduct = new String[MAX_ARRAY];
							arrFreeProduct[iRun] = detail.freeProductCode;
							mapArrayFreeProduct.put(detail.promotionCode, arrFreeProduct);
						} else {
							String[] arrFreeProduct = mapArrayFreeProduct.get(detail.promotionCode);
							arrFreeProduct[iRun] = detail.freeProductCode;
						}
						if (mapArrayFreeQuantityUnit.get(detail.promotionCode) == null) {
							Integer[] arrUnit = new Integer[MAX_ARRAY];
							arrUnit[iRun] = detail.freeProductUnit;
							mapArrayFreeQuantityUnit.put(detail.promotionCode, arrUnit);
						} else {
							Integer[] arrUnit = mapArrayFreeQuantityUnit.get(detail.promotionCode);
							arrUnit[iRun] = detail.freeProductUnit;
						}
						if (mapArrayFreeQuantity.get(detail.promotionCode) == null) {
							BigDecimal[] arrFreeQuantity = new BigDecimal[MAX_ARRAY];
							arrFreeQuantity[iRun] = detail.freeQuantity;
							mapArrayFreeQuantity.put(detail.promotionCode, arrFreeQuantity);
						} else {
							BigDecimal[] arrFreeProduct = mapArrayFreeQuantity.get(detail.promotionCode);
							arrFreeProduct[iRun] = detail.freeQuantity;
						}
						if (mapArrayDiscountAmount.get(detail.promotionCode) == null) {
							BigDecimal[] arrDiscountAmount = new BigDecimal[MAX_ARRAY];
							arrDiscountAmount[iRun] = detail.discountAmount;
							mapArrayDiscountAmount.put(detail.promotionCode, arrDiscountAmount);
						} else {
							BigDecimal[] arrDiscountAmount = mapArrayDiscountAmount.get(detail.promotionCode);
							arrDiscountAmount[iRun] = detail.discountAmount;
						}
						if (mapArrayDiscountPercent.get(detail.promotionCode) == null) {
							Float[] arrDiscountPercent = new Float[MAX_ARRAY];
							arrDiscountPercent[iRun] = detail.discountPercent;
							mapArrayDiscountPercent.put(detail.promotionCode, arrDiscountPercent);
						} else {
							Float[] arrDiscountPercent = mapArrayDiscountPercent.get(detail.promotionCode);
							arrDiscountPercent[iRun] = detail.discountPercent;
						}
						if (mapArrayAndOr.get(detail.promotionCode) == null) {
							Boolean[] arrAndOr = new Boolean[MAX_ARRAY];
							arrAndOr[iRun] = detail.andOr;
							mapArrayAndOr.put(detail.promotionCode, arrAndOr);
						} else {
							Boolean[] arrAndOr = mapArrayAndOr.get(detail.promotionCode);
							arrAndOr[iRun] = detail.andOr;
						}
						// map Ma nhom
						if(mapPromoGroupCode.get(detail.promotionCode) == null){
							String[] arrPromoGroupCode = new String[MAX_ARRAY];
							arrPromoGroupCode[iRun] = detail.promoGroupCode;
							mapPromoGroupCode.put(detail.promotionCode, arrPromoGroupCode);
						}else{
							String[] arrPromoGroupCode = mapPromoGroupCode.get(detail.promotionCode);
							arrPromoGroupCode[iRun] = detail.promoGroupCode;
						}
						// map Ten nhom
						if(mapPromoGroupName.get(detail.promotionCode) == null){
							String[] arrPromoGroupName = new String[MAX_ARRAY];
							arrPromoGroupName[iRun] = detail.promoGroupName;
							mapPromoGroupName.put(detail.promotionCode, arrPromoGroupName);
						}else{
							String[] arrPromoGroupName = mapPromoGroupName.get(detail.promotionCode);
							arrPromoGroupName[iRun] = detail.promoGroupName;
						}
						// map Ma muc
						if(mapPromoLevelCode.get(detail.promotionCode) == null){
							String[] arrayPromoLevelCode = new String[MAX_ARRAY];
							arrayPromoLevelCode[iRun] = detail.promoLevelCode;
							mapPromoLevelCode.put(detail.promotionCode, arrayPromoLevelCode);
						}else{
							String[] arrayPromoLevelCode = mapPromoLevelCode.get(detail.promotionCode);
							arrayPromoLevelCode[iRun] = detail.promoLevelCode;
						}
						// map Bội số
						/*if(mapMultiple.get(detail.promotionCode) == null){
							Integer[] arrMultiple = new Integer[MAX_ARRAY];
							arrMultiple[iRun] = detail.multiple;
							mapMultiple.put(detail.promotionCode, arrMultiple);
						}else{
							Integer[] arrMultiple = mapMultiple.get(detail.promotionCode);
							arrMultiple[iRun] = detail.multiple;
						}
						// map Tối ưu
						if(mapRecursive.get(detail.promotionCode) == null){
							Integer[] arrRecursive = new Integer[MAX_ARRAY];
							arrRecursive[iRun] = detail.recursive;
							mapRecursive.put(detail.promotionCode, arrRecursive);
						}else{
							Integer[] arrRecursive = mapRecursive.get(detail.promotionCode);
							arrRecursive[iRun] = detail.recursive;
						}
						// map ĐKGH
						if(mapDkgh.get(detail.promotionCode) == null){
							Integer[] arrDkgh = new Integer[MAX_ARRAY];
							arrDkgh[iRun] = detail.dkgh;
							mapDkgh.put(detail.promotionCode, arrDkgh);
						}else{
							Integer[] arrDkgh = mapDkgh.get(detail.promotionCode);
							arrDkgh[iRun] = detail.dkgh;
						}
						*/
						if (!StringUtil.isNullOrEmpty(detail.productCode) && lstProductPromo.get(detail.promotionCode + "-" + detail.productCode) == null) {
							lstProductPromo.put(detail.promotionCode + "-" + detail.productCode, indexProductPromo++);
						}
					} else {
						//error
						errRow.setContent13(messageError);
						lstDetailError.add(errRow);
						if (mapErrorPromotion.get(detail.promotionCode) == null) {
							mapErrorPromotion.put(detail.promotionCode, messageError);
						}
					}
					// totalItem++;
					iRun++;
				}
				/**
				 * put vao group level
				 */
				String messageError = "";
				CellBean errRow = new CellBean();
				
				for (String promotionProgramCode : mapPromotionType.keySet()) {
					Integer unit = -1;
					String[] arrPromoGroupCode = mapPromoGroupCode.get(promotionProgramCode);
					String[] arrPromoGroupName = mapPromoGroupName.get(promotionProgramCode);
				/*	String[] arrPromoLevelCode = mapPromoLevelCode.get(promotionProgramCode);
					Integer[] arrMultiple = mapMultiple.get(promotionProgramCode);
					Integer[] arrRecursive = mapRecursive.get(promotionProgramCode);		*/			
//					Integer[] arrDkgh = mapDkgh.get(promotionProgramCode);
					
					if (mapPromotionType.get(promotionProgramCode) == 1) {
						/**
						 * mua A(1), B(1) dc km ... C(1), D(1)
						 */
						String[] arrProduct = mapArrayProduct.get(promotionProgramCode);
						Integer[] arrProductUnit = mapArrayQuantityUnit.get(promotionProgramCode);
						BigDecimal[] arrSaleQuantity = mapArraySaleQuantity.get(promotionProgramCode);
						Boolean[] arrAndOr = mapArrayAndOr.get(promotionProgramCode);
						String[] arrFreeProduct = mapArrayFreeProduct.get(promotionProgramCode);
						Integer[] arrFreeProductUnit = mapArrayFreeQuantityUnit.get(promotionProgramCode);
						BigDecimal[] arrFreeQuantity = mapArrayFreeQuantity.get(promotionProgramCode);
						//Sort theo saleQuantity
						sortQuantityProduct(arrProduct, arrProductUnit, arrSaleQuantity, arrAndOr, arrFreeProduct, arrFreeProductUnit, arrFreeQuantity, null, null);
						for (int i = 0; arrProduct != null && i < arrProduct.length; i++) {
							if (!StringUtil.isNullOrEmpty(arrProduct[i]) && arrSaleQuantity[i] != null && !StringUtil.isNullOrEmpty(arrFreeProduct[i]) && arrFreeQuantity[i] != null) {
								GroupMua groupMua;
								GroupKM groupKM;
								unit = arrProductUnit[i];
								if (mapPromotionMua.get(promotionProgramCode) != null && mapPromotionKM.get(promotionProgramCode) != null) {
									groupMua = mapPromotionMua.get(promotionProgramCode).get(mapPromotionMua.get(promotionProgramCode).size() - 1);
									groupKM = mapPromotionKM.get(promotionProgramCode).get(mapPromotionKM.get(promotionProgramCode).size() - 1);
								} else {
									ListGroupMua lstGroupMua = new ListGroupMua();
									ListGroupKM lstGroupKM = new ListGroupKM();
									groupMua = new GroupMua();
									groupMua.groupCode = "N" + (lstGroupMua.size() + 1);
									groupMua.qttUnit = unit;
									
									//groupMua.groupCode = arrPromoGroupCode[i];
									groupMua.groupName = arrPromoGroupName[i];
//									groupMua.multiple = arrMultiple[i];
//									groupMua.recursive = arrRecursive[i];
//									groupMua.dkgh = arrDkgh[i];
									
									groupKM = new GroupKM();
									groupKM.groupCode = "N" + (lstGroupKM.size() + 1);
									groupKM.qttUnit = arrFreeProductUnit[i];
									
									//groupKM.groupCode = arrPromoGroupCode[i];
									groupKM.groupName = arrPromoGroupName[i];
//									groupKM.multiple = arrMultiple[i];
//									groupKM.recursive = arrRecursive[i];
//									groupKM.dkgh = arrDkgh[i];
									
									groupMua.order = lstGroupMua.size() + 1;
									lstGroupMua.add(groupMua);
									groupKM.order = lstGroupKM.size() + 1;
									lstGroupKM.add(groupKM);
									mapPromotionMua.put(promotionProgramCode, lstGroupMua);
									mapPromotionKM.put(promotionProgramCode, lstGroupKM);
								}
								GroupSP groupSPMua = groupMua.add2Level(mapPromotionTypeCheck.get(promotionProgramCode), arrProduct[i], unit, arrSaleQuantity[i], indexMua++, i, arrProduct, arrSaleQuantity, arrFreeProduct, arrFreeQuantity);
								if (groupSPMua == null) {
									groupMua = new GroupMua();
									List<GroupMua> lstGroupMua = mapPromotionMua.get(promotionProgramCode);
									lstGroupMua.add(groupMua);
									groupSPMua = groupMua.add2Level(mapPromotionTypeCheck.get(promotionProgramCode), arrProduct[i], unit, arrSaleQuantity[i], indexMua++, i, arrProduct, arrSaleQuantity, arrFreeProduct, arrFreeQuantity);
								}
								List<Long> lstIndex = mapMuaKM.get(groupSPMua.index);//lay danh sach cac index muc duoc map voi muc sp mua
								List<GroupSP> lstLevelKM = groupKM.searchIndex(lstIndex);//lay danh sach cac muc duoc map voi muc sp mua
								GroupSP groupSPKM = groupKM.add2Level(mapPromotionTypeCheck.get(promotionProgramCode), arrFreeProduct[i], unit, arrFreeQuantity[i], arrAndOr[i], indexKM++, i, arrFreeProduct, arrFreeQuantity, lstLevelKM);
								if (groupSPKM == null) {
									groupKM = new GroupKM();
									List<GroupKM> lstGroupKM = mapPromotionKM.get(promotionProgramCode);
									lstGroupKM.add(groupKM);
									groupSPKM = groupKM.add2Level(mapPromotionTypeCheck.get(promotionProgramCode), arrFreeProduct[i], unit, arrFreeQuantity[i], arrAndOr[i], indexKM++, i, arrFreeProduct, arrFreeQuantity, null);
								}
								mapMuaKM.put(groupSPMua.index, groupSPKM.index);
							}
						}
					} else if (mapPromotionType.get(promotionProgramCode) == 2) {
						/**
						 * mua A(1), B(1) dc km ... 10.000
						 */
						String[] arrProduct = mapArrayProduct.get(promotionProgramCode);
						Integer[] arrProductUnit = mapArrayQuantityUnit.get(promotionProgramCode);
						BigDecimal[] arrSaleQuantity = mapArraySaleQuantity.get(promotionProgramCode);
						Boolean[] arrAndOr = mapArrayAndOr.get(promotionProgramCode);
						BigDecimal[] arrFreeAmount = mapArrayDiscountAmount.get(promotionProgramCode);
						//Sort theo saleQuantity
						sortQuantityProduct(arrProduct, arrProductUnit, arrSaleQuantity, arrAndOr, null, null, null, arrFreeAmount, null);
						for (int i = 0; arrProduct != null && i < arrProduct.length; i++) {
							if (!StringUtil.isNullOrEmpty(arrProduct[i]) && arrSaleQuantity[i] != null && arrFreeAmount[i] != null) {
								GroupMua groupMua;
								GroupKM groupKM;
								unit = arrProductUnit[i];
								if (mapPromotionMua.get(promotionProgramCode) != null && mapPromotionKM.get(promotionProgramCode) != null) {
									groupMua = mapPromotionMua.get(promotionProgramCode).get(mapPromotionMua.get(promotionProgramCode).size() - 1);
									groupKM = mapPromotionKM.get(promotionProgramCode).get(mapPromotionKM.get(promotionProgramCode).size() - 1);
								} else {
									ListGroupMua lstGroupMua = new ListGroupMua();
									ListGroupKM lstGroupKM = new ListGroupKM();
									groupMua = new GroupMua();
									groupMua.groupCode = "N" + (lstGroupMua.size() + 1);
									groupMua.qttUnit = unit;
									
									//groupMua.groupCode = arrPromoGroupCode[i];
									groupMua.groupName = arrPromoGroupName[i];
//									groupMua.multiple = arrMultiple[i];
//									groupMua.recursive = arrRecursive[i];
//									groupMua.dkgh = arrDkgh[i];
									
									groupKM = new GroupKM();
									groupKM.groupCode = "N" + (lstGroupKM.size() + 1);
									//groupKM.groupCode = arrPromoGroupCode[i];
									groupKM.groupName = arrPromoGroupName[i];
//									groupKM.multiple = arrMultiple[i];
//									groupKM.recursive = arrRecursive[i];
//									groupKM.dkgh = arrDkgh[i];
									
									groupMua.order = lstGroupMua.size() + 1;
									lstGroupMua.add(groupMua);
									groupKM.order = lstGroupKM.size() + 1;
									lstGroupKM.add(groupKM);
									mapPromotionMua.put(promotionProgramCode, lstGroupMua);
									mapPromotionKM.put(promotionProgramCode, lstGroupKM);
								}
								GroupSP groupSPMua = groupMua.add2Level(mapPromotionTypeCheck.get(promotionProgramCode), arrProduct[i], unit, arrSaleQuantity[i], indexMua++, i, arrProduct, arrSaleQuantity, null, null);
								if (groupSPMua == null) {
									groupMua = new GroupMua();
									List<GroupMua> lstGroupMua = mapPromotionMua.get(promotionProgramCode);
									lstGroupMua.add(groupMua);
									groupSPMua = groupMua.add2Level(mapPromotionTypeCheck.get(promotionProgramCode), arrProduct[i], unit, arrSaleQuantity[i], indexMua++, i, arrProduct, arrSaleQuantity, null, null);
								}
								List<Long> lstIndex = mapMuaKM.get(groupSPMua.index);//lay danh sach cac index muc duoc map voi muc sp mua
								List<GroupSP> lstLevelKM = groupKM.searchIndex(lstIndex);//lay danh sach cac muc duoc map voi muc sp mua
								GroupSP groupSPKM = groupKM.add2Level(arrFreeAmount[i], indexKM++, i, arrFreeAmount, lstLevelKM);
								if (groupSPKM == null) {
									groupKM = new GroupKM();
									List<GroupKM> lstGroupKM = mapPromotionKM.get(promotionProgramCode);
									lstGroupKM.add(groupKM);
									groupSPKM = groupKM.add2Level(arrFreeAmount[i], indexKM++, i, arrFreeAmount, lstLevelKM);
								}
								mapMuaKM.put(groupSPMua.index, groupSPKM.index);
							}
						}
					} else if (mapPromotionType.get(promotionProgramCode) == 3) {
						/**
						 * mua A(1), B(1) dc km ... 10%
						 */
						String[] arrProduct = mapArrayProduct.get(promotionProgramCode);
						Integer[] arrProductUnit = mapArrayQuantityUnit.get(promotionProgramCode);
						BigDecimal[] arrSaleQuantity = mapArraySaleQuantity.get(promotionProgramCode);
						Boolean[] arrAndOr = mapArrayAndOr.get(promotionProgramCode);
						Float[] arrPercent = mapArrayDiscountPercent.get(promotionProgramCode);
						//Sort theo saleQuantity
						sortQuantityProduct(arrProduct, arrProductUnit, arrSaleQuantity, arrAndOr, null, null, null, null, arrPercent);
						for (int i = 0; arrProduct != null && i < arrProduct.length; i++) {
							if (!StringUtil.isNullOrEmpty(arrProduct[i]) && arrSaleQuantity[i] != null && arrPercent[i] != null) {
								GroupMua groupMua;
								GroupKM groupKM;
								unit = arrProductUnit[i];
								if (mapPromotionMua.get(promotionProgramCode) != null && mapPromotionKM.get(promotionProgramCode) != null) {
									groupMua = mapPromotionMua.get(promotionProgramCode).get(mapPromotionMua.get(promotionProgramCode).size() - 1);
									groupKM = mapPromotionKM.get(promotionProgramCode).get(mapPromotionKM.get(promotionProgramCode).size() - 1);
								} else {
									ListGroupMua lstGroupMua = new ListGroupMua();
									ListGroupKM lstGroupKM = new ListGroupKM();
									groupMua = new GroupMua();
									groupMua.groupCode = "N" + (lstGroupMua.size() + 1);
									groupMua.qttUnit = unit;
									//groupMua.groupCode = arrPromoGroupCode[i];
									groupMua.groupName = arrPromoGroupName[i];
//									groupMua.multiple = arrMultiple[i];
//									groupMua.recursive = arrRecursive[i];
//									groupMua.dkgh = arrDkgh[i];
									
									groupKM = new GroupKM();
									groupKM.groupCode = "N" + (lstGroupKM.size() + 1);
									//groupKM.groupCode = arrPromoGroupCode[i];
									groupKM.groupName = arrPromoGroupName[i];
//									groupKM.multiple = arrMultiple[i];
//									groupKM.recursive = arrRecursive[i];
//									groupKM.dkgh = arrDkgh[i];
//									
									groupMua.order = lstGroupMua.size() + 1;
									lstGroupMua.add(groupMua);
									groupKM.order = lstGroupKM.size() + 1;
									lstGroupKM.add(groupKM);
									mapPromotionMua.put(promotionProgramCode, lstGroupMua);
									mapPromotionKM.put(promotionProgramCode, lstGroupKM);
								}
								GroupSP groupSPMua = groupMua.add2Level(mapPromotionTypeCheck.get(promotionProgramCode), arrProduct[i], unit, arrSaleQuantity[i], indexMua++, i, arrProduct, arrSaleQuantity, null, null);
								if (groupSPMua == null) {
									groupMua = new GroupMua();
									List<GroupMua> lstGroupMua = mapPromotionMua.get(promotionProgramCode);
									lstGroupMua.add(groupMua);
									groupSPMua = groupMua.add2Level(mapPromotionTypeCheck.get(promotionProgramCode), arrProduct[i], unit, arrSaleQuantity[i], indexMua++, i, arrProduct, arrSaleQuantity, null, null);
								}
								List<Long> lstIndex = mapMuaKM.get(groupSPMua.index);//lay danh sach cac index muc duoc map voi muc sp mua
								List<GroupSP> lstLevelKM = groupKM.searchIndex(lstIndex);//lay danh sach cac muc duoc map voi muc sp mua
								GroupSP groupSPKM = groupKM.add2Level(arrPercent[i], indexKM++, i, arrPercent, lstLevelKM);
								if (groupSPKM == null) {
									groupKM = new GroupKM();
									List<GroupKM> lstGroupKM = mapPromotionKM.get(promotionProgramCode);
									lstGroupKM.add(groupKM);
									groupSPKM = groupKM.add2Level(arrPercent[i], indexKM++, i, arrPercent, lstLevelKM);
								}
								mapMuaKM.put(groupSPMua.index, groupSPKM.index);
							}
						}
					} else if (mapPromotionType.get(promotionProgramCode) == 4) {
						/**
						 * mua A(10.000), B(10.000) dc km ... C(1), D(1)
						 */
						String[] arrProduct = mapArrayProduct.get(promotionProgramCode);
						Integer[] arrProductUnit = mapArrayQuantityUnit.get(promotionProgramCode);
						BigDecimal[] arrSaleAmount = mapArraySaleAmount.get(promotionProgramCode);
						Boolean[] arrAndOr = mapArrayAndOr.get(promotionProgramCode);
						String[] arrFreeProduct = mapArrayFreeProduct.get(promotionProgramCode);
						Integer[] arrFreeProductUnit = mapArrayFreeQuantityUnit.get(promotionProgramCode);
						BigDecimal[] arrFreeQuantity = mapArrayFreeQuantity.get(promotionProgramCode);
						//Sort theo saleQuantity
						sortAmountProduct(arrProduct, arrProductUnit, arrSaleAmount, arrAndOr, arrFreeProduct, arrFreeProductUnit, arrFreeQuantity, null, null);
						for (int i = 0; arrProduct != null && i < arrProduct.length; i++) {
							if (!StringUtil.isNullOrEmpty(arrProduct[i]) && arrSaleAmount[i] != null && !StringUtil.isNullOrEmpty(arrFreeProduct[i]) && arrFreeQuantity[i] != null) {
								GroupMua groupMua;
								GroupKM groupKM;
								unit = arrProductUnit[i];
								if (mapPromotionMua.get(promotionProgramCode) != null && mapPromotionKM.get(promotionProgramCode) != null) {
									groupMua = mapPromotionMua.get(promotionProgramCode).get(mapPromotionMua.get(promotionProgramCode).size() - 1);
									groupKM = mapPromotionKM.get(promotionProgramCode).get(mapPromotionKM.get(promotionProgramCode).size() - 1);
								} else {
									ListGroupMua lstGroupMua = new ListGroupMua();
									ListGroupKM lstGroupKM = new ListGroupKM();
									groupMua = new GroupMua();
									groupMua.groupCode = "N" + (lstGroupMua.size() + 1);
									groupMua.qttUnit = unit;
									//groupMua.groupCode = arrPromoGroupCode[i];
									groupMua.groupName = arrPromoGroupName[i];
//									groupMua.multiple = arrMultiple[i];
//									groupMua.recursive = arrRecursive[i];
//									groupMua.dkgh = arrDkgh[i];
									
									groupKM = new GroupKM();
									groupKM.groupCode = "N" + (lstGroupKM.size() + 1);
									//groupKM.groupCode = arrPromoGroupCode[i];
									groupKM.groupName = arrPromoGroupName[i];
//									groupKM.multiple = arrMultiple[i];
//									groupKM.recursive = arrRecursive[i];
//									groupKM.dkgh = arrDkgh[i];
									
									groupMua.qttUnit = arrFreeProductUnit[i];
									groupMua.order = lstGroupMua.size() + 1;
									lstGroupMua.add(groupMua);
									groupKM.order = lstGroupKM.size() + 1;
									lstGroupKM.add(groupKM);
									mapPromotionMua.put(promotionProgramCode, lstGroupMua);
									mapPromotionKM.put(promotionProgramCode, lstGroupKM);
								}
								GroupSP groupSPMua = groupMua.add2Level(mapPromotionTypeCheck.get(promotionProgramCode), arrProduct[i], arrSaleAmount[i], indexMua++, i, arrProduct, arrSaleAmount, arrFreeProduct, arrFreeQuantity);
								if (groupSPMua == null) {
									groupMua = new GroupMua();
									List<GroupMua> lstGroupMua = mapPromotionMua.get(promotionProgramCode);
									lstGroupMua.add(groupMua);
									groupSPMua = groupMua.add2Level(mapPromotionTypeCheck.get(promotionProgramCode), arrProduct[i], arrSaleAmount[i], indexMua++, i, arrProduct, arrSaleAmount, arrFreeProduct, arrFreeQuantity);
								}
								List<Long> lstIndex = mapMuaKM.get(groupSPMua.index);//lay danh sach cac index muc duoc map voi muc sp mua
								List<GroupSP> lstLevelKM = groupKM.searchIndex(lstIndex);//lay danh sach cac muc duoc map voi muc sp mua
								GroupSP groupSPKM = groupKM.add2Level(mapPromotionTypeCheck.get(promotionProgramCode), arrFreeProduct[i], unit, arrFreeQuantity[i], arrAndOr[i], indexKM++, i, arrFreeProduct, arrFreeQuantity, lstLevelKM);
								if (groupSPKM == null) {
									groupKM = new GroupKM();
									List<GroupKM> lstGroupKM = mapPromotionKM.get(promotionProgramCode);
									lstGroupKM.add(groupKM);
									groupSPKM = groupKM.add2Level(mapPromotionTypeCheck.get(promotionProgramCode), arrFreeProduct[i], unit, arrFreeQuantity[i], arrAndOr[i], indexKM++, i, arrFreeProduct, arrFreeQuantity, null);
								}
								mapMuaKM.put(groupSPMua.index, groupSPKM.index);
							}
						}
					} else if (mapPromotionType.get(promotionProgramCode) == 5) {
						/**
						 * mua A(10.000), B(10.000) dc km ... 10.000
						 */
						String[] arrProduct = mapArrayProduct.get(promotionProgramCode);
						Integer[] arrProductUnit = mapArrayQuantityUnit.get(promotionProgramCode);
						BigDecimal[] arrSaleAmount = mapArraySaleAmount.get(promotionProgramCode);
						Boolean[] arrAndOr = mapArrayAndOr.get(promotionProgramCode);
						BigDecimal[] arrFreeAmount = mapArrayDiscountAmount.get(promotionProgramCode);
						//Sort theo saleQuantity
						sortAmountProduct(arrProduct, arrProductUnit, arrSaleAmount, arrAndOr, null, null, null, arrFreeAmount, null);
						for (int i = 0; arrProduct != null && i < arrProduct.length; i++) {
							if (!StringUtil.isNullOrEmpty(arrProduct[i]) && arrSaleAmount[i] != null && arrFreeAmount[i] != null) {
								GroupMua groupMua;
								GroupKM groupKM;
								unit = arrProductUnit[i];
								if (mapPromotionMua.get(promotionProgramCode) != null && mapPromotionKM.get(promotionProgramCode) != null) {
									groupMua = mapPromotionMua.get(promotionProgramCode).get(mapPromotionMua.get(promotionProgramCode).size() - 1);
									groupKM = mapPromotionKM.get(promotionProgramCode).get(mapPromotionKM.get(promotionProgramCode).size() - 1);
								} else {
									ListGroupMua lstGroupMua = new ListGroupMua();
									ListGroupKM lstGroupKM = new ListGroupKM();
									groupMua = new GroupMua();
									groupMua.groupCode = "N" + (lstGroupMua.size() + 1);
									groupMua.qttUnit = unit;
									//groupMua.groupCode = arrPromoGroupCode[i];
									groupMua.groupName = arrPromoGroupName[i];
//									groupMua.multiple = arrMultiple[i];
//									groupMua.recursive = arrRecursive[i];
//									groupMua.dkgh = arrDkgh[i];
									
									groupKM = new GroupKM();
									groupKM.groupCode = "N" + (lstGroupKM.size() + 1);
									//groupKM.groupCode = arrPromoGroupCode[i];
									groupKM.groupName = arrPromoGroupName[i];
//									groupKM.multiple = arrMultiple[i];
//									groupKM.recursive = arrRecursive[i];
//									groupKM.dkgh = arrDkgh[i];
									
									groupMua.order = lstGroupMua.size() + 1;
									lstGroupMua.add(groupMua);
									groupKM.order = lstGroupKM.size() + 1;
									lstGroupKM.add(groupKM);
									mapPromotionMua.put(promotionProgramCode, lstGroupMua);
									mapPromotionKM.put(promotionProgramCode, lstGroupKM);
								}
								GroupSP groupSPMua = groupMua.add2Level(mapPromotionTypeCheck.get(promotionProgramCode), arrProduct[i], arrSaleAmount[i], indexMua++, i, arrProduct, arrSaleAmount, null, null);
								if (groupSPMua == null) {
									groupMua = new GroupMua();
									List<GroupMua> lstGroupMua = mapPromotionMua.get(promotionProgramCode);
									lstGroupMua.add(groupMua);
									groupSPMua = groupMua.add2Level(mapPromotionTypeCheck.get(promotionProgramCode), arrProduct[i], arrSaleAmount[i], indexMua++, i, arrProduct, arrSaleAmount, null, null);
								}
								List<Long> lstIndex = mapMuaKM.get(groupSPMua.index);//lay danh sach cac index muc duoc map voi muc sp mua
								List<GroupSP> lstLevelKM = groupKM.searchIndex(lstIndex);//lay danh sach cac muc duoc map voi muc sp mua
								GroupSP groupSPKM = groupKM.add2Level(arrFreeAmount[i], indexKM++, i, arrFreeAmount, lstLevelKM);
								if (groupSPKM == null) {
									groupKM = new GroupKM();
									List<GroupKM> lstGroupKM = mapPromotionKM.get(promotionProgramCode);
									lstGroupKM.add(groupKM);
									groupSPKM = groupKM.add2Level(arrFreeAmount[i], indexKM++, i, arrFreeAmount, lstLevelKM);
								}
								mapMuaKM.put(groupSPMua.index, groupSPKM.index);
							}
						}
					} else if (mapPromotionType.get(promotionProgramCode) == 6) {
						/**
						 * mua A(10.000), B(10.000) dc km ... 10%
						 */
						String[] arrProduct = mapArrayProduct.get(promotionProgramCode);
						Integer[] arrProductUnit = mapArrayQuantityUnit.get(promotionProgramCode);
						BigDecimal[] arrSaleAmount = mapArraySaleAmount.get(promotionProgramCode);
						Boolean[] arrAndOr = mapArrayAndOr.get(promotionProgramCode);
						Float[] arrPercent = mapArrayDiscountPercent.get(promotionProgramCode);
						//Sort theo saleQuantity
						sortAmountProduct(arrProduct, arrProductUnit, arrSaleAmount, arrAndOr, null, null, null, null, arrPercent);
						for (int i = 0; arrProduct != null && i < arrProduct.length; i++) {
							if (!StringUtil.isNullOrEmpty(arrProduct[i]) && arrSaleAmount[i] != null && arrPercent[i] != null) {
								GroupMua groupMua;
								GroupKM groupKM;
								unit = arrProductUnit[i];
								if (mapPromotionMua.get(promotionProgramCode) != null && mapPromotionKM.get(promotionProgramCode) != null) {
									groupMua = mapPromotionMua.get(promotionProgramCode).get(mapPromotionMua.get(promotionProgramCode).size() - 1);
									groupKM = mapPromotionKM.get(promotionProgramCode).get(mapPromotionKM.get(promotionProgramCode).size() - 1);
								} else {
									ListGroupMua lstGroupMua = new ListGroupMua();
									ListGroupKM lstGroupKM = new ListGroupKM();
									groupMua = new GroupMua();
									groupMua.groupCode = "N" + (lstGroupMua.size() + 1);
									//groupMua.groupCode = arrPromoGroupCode[i];
									groupMua.groupName = arrPromoGroupName[i];
//									groupMua.multiple = arrMultiple[i];
//									groupMua.recursive = arrRecursive[i];
//									groupMua.dkgh = arrDkgh[i];
									
									groupMua.qttUnit = unit;
									groupKM = new GroupKM();
									groupKM.groupCode = "N" + (lstGroupKM.size() + 1);
									//groupKM.groupCode = arrPromoGroupCode[i];
									groupKM.groupName = arrPromoGroupName[i];
//									groupKM.multiple = arrMultiple[i];
//									groupKM.recursive = arrRecursive[i];
//									groupKM.dkgh = arrDkgh[i];
									
									groupMua.order = lstGroupMua.size() + 1;
									lstGroupMua.add(groupMua);
									groupKM.order = lstGroupKM.size() + 1;
									lstGroupKM.add(groupKM);
									mapPromotionMua.put(promotionProgramCode, lstGroupMua);
									mapPromotionKM.put(promotionProgramCode, lstGroupKM);
								}
								GroupSP groupSPMua = groupMua.add2Level(mapPromotionTypeCheck.get(promotionProgramCode), arrProduct[i], arrSaleAmount[i], indexMua++, i, arrProduct, arrSaleAmount, null, null);
								if (groupSPMua == null) {
									groupMua = new GroupMua();
									List<GroupMua> lstGroupMua = mapPromotionMua.get(promotionProgramCode);
									lstGroupMua.add(groupMua);
									groupSPMua = groupMua.add2Level(mapPromotionTypeCheck.get(promotionProgramCode), arrProduct[i], arrSaleAmount[i], indexMua++, i, arrProduct, arrSaleAmount, null, null);
								}
								List<Long> lstIndex = mapMuaKM.get(groupSPMua.index);//lay danh sach cac index muc duoc map voi muc sp mua
								List<GroupSP> lstLevelKM = groupKM.searchIndex(lstIndex);//lay danh sach cac muc duoc map voi muc sp mua
								GroupSP groupSPKM = groupKM.add2Level(arrPercent[i], indexKM++, i, arrPercent, lstLevelKM);
								if (groupSPKM == null) {
									groupKM = new GroupKM();
									List<GroupKM> lstGroupKM = mapPromotionKM.get(promotionProgramCode);
									lstGroupKM.add(groupKM);
									groupSPKM = groupKM.add2Level(arrPercent[i], indexKM++, i, arrPercent, lstLevelKM);
								}
								mapMuaKM.put(groupSPMua.index, groupSPKM.index);
							}
						}
					} else if (mapPromotionType.get(promotionProgramCode) == 7) {
						/**
						 * mua 10000 dc km ... C(1), D(1)
						 */
						BigDecimal[] arrSaleAmount = mapArraySaleAmount.get(promotionProgramCode);
						String[] arrFreeProduct = mapArrayFreeProduct.get(promotionProgramCode);
						Integer[] arrFreeProductUnit = mapArrayFreeQuantityUnit.get(promotionProgramCode);
						BigDecimal[] arrFreeQuantity = mapArrayFreeQuantity.get(promotionProgramCode);
						Boolean[] arrAndOr = mapArrayAndOr.get(promotionProgramCode);
						sortAmount(arrSaleAmount, arrAndOr, arrFreeProduct, arrFreeProductUnit, arrFreeQuantity, null, null);
						for (int i = 0; arrSaleAmount != null && i < arrSaleAmount.length; i++) {
							if (arrSaleAmount[i] != null && !StringUtil.isNullOrEmpty(arrFreeProduct[i]) && arrFreeQuantity[i] != null) {
								GroupMua groupMua;
								GroupKM groupKM;
								if (mapPromotionMua.get(promotionProgramCode) != null && mapPromotionKM.get(promotionProgramCode) != null) {
									groupMua = mapPromotionMua.get(promotionProgramCode).get(mapPromotionMua.get(promotionProgramCode).size() - 1);
									groupKM = mapPromotionKM.get(promotionProgramCode).get(mapPromotionKM.get(promotionProgramCode).size() - 1);
								} else {
									ListGroupMua lstGroupMua = new ListGroupMua();
									ListGroupKM lstGroupKM = new ListGroupKM();
									groupMua = new GroupMua();
									groupMua.groupCode = "N" + (lstGroupMua.size() + 1);
									//groupMua.groupCode = arrPromoGroupCode[i];
									groupMua.groupName = arrPromoGroupName[i];
//									groupMua.multiple = arrMultiple[i];
//									groupMua.recursive = arrRecursive[i];
//									groupMua.dkgh = arrDkgh[i];
									
									groupKM = new GroupKM();
									groupKM.groupCode = "N" + (lstGroupKM.size() + 1);
									//groupKM.groupCode = arrPromoGroupCode[i];
									groupKM.groupName = arrPromoGroupName[i];
//									groupKM.multiple = arrMultiple[i];
//									groupKM.recursive = arrRecursive[i];
//									groupKM.dkgh = arrDkgh[i];
									
									groupKM.qttUnit = arrFreeProductUnit[i];
									groupMua.order = lstGroupMua.size() + 1;
									lstGroupMua.add(groupMua);
									groupKM.order = lstGroupKM.size() + 1;
									lstGroupKM.add(groupKM);
									mapPromotionMua.put(promotionProgramCode, lstGroupMua);
									mapPromotionKM.put(promotionProgramCode, lstGroupKM);
								}
								GroupSP groupSPMua = groupMua.add2Level(2, arrSaleAmount[i], null, indexMua++, i, arrSaleAmount, arrFreeProduct, arrFreeQuantity);
								if (groupSPMua == null) {
									groupMua = new GroupMua();
									List<GroupMua> lstGroupMua = mapPromotionMua.get(promotionProgramCode);
									lstGroupMua.add(groupMua);
									groupSPMua = groupMua.add2Level(2, arrSaleAmount[i], null, indexMua++, i, arrSaleAmount, arrFreeProduct, arrFreeQuantity);
								}
								List<Long> lstIndex = mapMuaKM.get(groupSPMua.index);//lay danh sach cac index muc duoc map voi muc sp mua
								List<GroupSP> lstLevelKM = groupKM.searchIndex(lstIndex);//lay danh sach cac muc duoc map voi muc sp mua
								GroupSP groupSPKM = groupKM.add2Level(mapPromotionTypeCheck.get(promotionProgramCode), arrFreeProduct[i], unit, arrFreeQuantity[i], arrAndOr[i], indexKM++, i, arrFreeProduct, arrFreeQuantity, lstLevelKM);
								if (groupSPKM == null) {
									groupKM = new GroupKM();
									List<GroupKM> lstGroupKM = mapPromotionKM.get(promotionProgramCode);
									lstGroupKM.add(groupKM);
									groupSPKM = groupKM.add2Level(mapPromotionTypeCheck.get(promotionProgramCode), arrFreeProduct[i], unit, arrFreeQuantity[i], arrAndOr[i], indexKM++, i, arrFreeProduct, arrFreeQuantity, null);
								}
								mapMuaKM.put(groupSPMua.index, groupSPKM.index);
							}
						}
					} else if (mapPromotionType.get(promotionProgramCode) == 8) {
						/**
						 * mua 10000 dc km ... 10.000
						 */
						BigDecimal[] arrSaleAmount = mapArraySaleAmount.get(promotionProgramCode);
						BigDecimal[] arrFreeAmount = mapArrayDiscountAmount.get(promotionProgramCode);
						sortAmount(arrSaleAmount, null, null, null, null, arrFreeAmount, null);
						for (int i = 0; arrSaleAmount != null && i < arrSaleAmount.length; i++) {
							if (arrSaleAmount[i] != null && arrFreeAmount[i] != null) {
								GroupMua groupMua;
								GroupKM groupKM;
								if (mapPromotionMua.get(promotionProgramCode) != null && mapPromotionKM.get(promotionProgramCode) != null) {
									groupMua = mapPromotionMua.get(promotionProgramCode).get(mapPromotionMua.get(promotionProgramCode).size() - 1);
									groupKM = mapPromotionKM.get(promotionProgramCode).get(mapPromotionKM.get(promotionProgramCode).size() - 1);
								} else {
									ListGroupMua lstGroupMua = new ListGroupMua();
									ListGroupKM lstGroupKM = new ListGroupKM();
									groupMua = new GroupMua();
									groupMua.groupCode = "N" + (lstGroupMua.size() + 1);
									//groupMua.groupCode = arrPromoGroupCode[i];
									groupMua.groupName = arrPromoGroupName[i];
//									groupMua.multiple = arrMultiple[i];
//									groupMua.recursive = arrRecursive[i];
//									groupMua.dkgh = arrDkgh[i];
									
									groupKM = new GroupKM();
									groupKM.groupCode = "N" + (lstGroupKM.size() + 1);
									//groupKM.groupCode = arrPromoGroupCode[i];
									groupKM.groupName = arrPromoGroupName[i];
//									groupKM.multiple = arrMultiple[i];
//									groupKM.recursive = arrRecursive[i];
//									groupKM.dkgh = arrDkgh[i];
									
									groupMua.order = lstGroupMua.size() + 1;
									lstGroupMua.add(groupMua);
									groupKM.order = lstGroupKM.size() + 1;
									lstGroupKM.add(groupKM);
									mapPromotionMua.put(promotionProgramCode, lstGroupMua);
									mapPromotionKM.put(promotionProgramCode, lstGroupKM);
								}
								GroupSP groupSPMua = groupMua.add2Level(2, arrSaleAmount[i], null, indexMua++, i, arrSaleAmount, null, null);
								if (groupSPMua == null) {
									groupMua = new GroupMua();
									List<GroupMua> lstGroupMua = mapPromotionMua.get(promotionProgramCode);
									lstGroupMua.add(groupMua);
									groupSPMua = groupMua.add2Level(2, arrSaleAmount[i], null, indexMua++, i, arrSaleAmount, null, null);
								}
								List<Long> lstIndex = mapMuaKM.get(groupSPMua.index);//lay danh sach cac index muc duoc map voi muc sp mua
								List<GroupSP> lstLevelKM = groupKM.searchIndex(lstIndex);//lay danh sach cac muc duoc map voi muc sp mua
								GroupSP groupSPKM = groupKM.add2Level(arrFreeAmount[i], indexKM++, i, arrFreeAmount, lstLevelKM);
								if (groupSPKM == null) {
									groupKM = new GroupKM();
									List<GroupKM> lstGroupKM = mapPromotionKM.get(promotionProgramCode);
									lstGroupKM.add(groupKM);
									groupSPKM = groupKM.add2Level(arrFreeAmount[i], indexKM++, i, arrFreeAmount, lstLevelKM);
								}
								mapMuaKM.put(groupSPMua.index, groupSPKM.index);
							}
						}
					} else if (mapPromotionType.get(promotionProgramCode) == 9) {
						/**
						 * mua 10000 dc km ... 10%
						 */
						BigDecimal[] arrSaleAmount = mapArraySaleAmount.get(promotionProgramCode);
						Float[] arrPercent = mapArrayDiscountPercent.get(promotionProgramCode);
						sortAmount(arrSaleAmount, null, null, null, null, null, arrPercent);
						for (int i = 0; arrSaleAmount != null && i < arrSaleAmount.length; i++) {
							if (arrSaleAmount[i] != null && arrPercent[i] != null) {
								GroupMua groupMua;
								GroupKM groupKM;
								if (mapPromotionMua.get(promotionProgramCode) != null && mapPromotionKM.get(promotionProgramCode) != null) {
									groupMua = mapPromotionMua.get(promotionProgramCode).get(mapPromotionMua.get(promotionProgramCode).size() - 1);
									groupKM = mapPromotionKM.get(promotionProgramCode).get(mapPromotionKM.get(promotionProgramCode).size() - 1);
								} else {
									ListGroupMua lstGroupMua = new ListGroupMua();
									ListGroupKM lstGroupKM = new ListGroupKM();
									groupMua = new GroupMua();
									groupMua.groupCode = "N" + (lstGroupMua.size() + 1);
									//groupMua.groupCode = arrPromoGroupCode[i];
									groupMua.groupName = arrPromoGroupName[i];
									/*groupMua.multiple = arrMultiple[i];
									groupMua.recursive = arrRecursive[i];
									groupMua.dkgh = arrDkgh[i];*/
									
									groupKM = new GroupKM();
									groupKM.groupCode = "N" + (lstGroupKM.size() + 1);
									//groupKM.groupCode = arrPromoGroupCode[i];
									groupKM.groupName = arrPromoGroupName[i];
								/*	groupKM.multiple = arrMultiple[i];
									groupKM.recursive = arrRecursive[i];
									groupKM.dkgh = arrDkgh[i];*/
									
									groupMua.order = lstGroupMua.size() + 1;									
									lstGroupMua.add(groupMua);
									groupKM.order = lstGroupKM.size() + 1;
									lstGroupKM.add(groupKM);
									mapPromotionMua.put(promotionProgramCode, lstGroupMua);
									mapPromotionKM.put(promotionProgramCode, lstGroupKM);
								}
								GroupSP groupSPMua = groupMua.add2Level(2, arrSaleAmount[i], null, indexMua++, i, arrSaleAmount, null, null);
								if (groupSPMua == null) {
									groupMua = new GroupMua();
									List<GroupMua> lstGroupMua = mapPromotionMua.get(promotionProgramCode);
									lstGroupMua.add(groupMua);
									groupSPMua = groupMua.add2Level(2, arrSaleAmount[i], null, indexMua++, i, arrSaleAmount, null, null);
								}
								List<Long> lstIndex = mapMuaKM.get(groupSPMua.index);//lay danh sach cac index muc duoc map voi muc sp mua
								List<GroupSP> lstLevelKM = groupKM.searchIndex(lstIndex);//lay danh sach cac muc duoc map voi muc sp mua
								GroupSP groupSPKM = groupKM.add2Level(arrPercent[i], indexKM++, i, arrPercent, lstLevelKM);
								if (groupSPKM == null) {
									groupKM = new GroupKM();
									List<GroupKM> lstGroupKM = mapPromotionKM.get(promotionProgramCode);
									lstGroupKM.add(groupKM);
									groupSPKM = groupKM.add2Level(arrPercent[i], indexKM++, i, arrPercent, lstLevelKM);
								}
								mapMuaKM.put(groupSPMua.index, groupSPKM.index);
							}
						}
					} else if (mapPromotionType.get(promotionProgramCode) == 10) {
						/**
						 * ZV24
						 */
						BigDecimal[] arrSaleQuantity = mapArraySaleQuantity.get(promotionProgramCode);
						Integer[] arrProductUnit = mapArrayQuantityUnit.get(promotionProgramCode);
						String[] arrFreeProduct = mapArrayFreeProduct.get(promotionProgramCode);
						BigDecimal[] arrFreeQuantity = mapArrayFreeQuantity.get(promotionProgramCode);
						Boolean[] arrAndOr = mapArrayAndOr.get(promotionProgramCode);
						Integer[] arrFreeProductUnit = mapArrayFreeQuantityUnit.get(promotionProgramCode);
						sortQuantity(arrSaleQuantity, arrAndOr, arrFreeProduct, arrFreeProductUnit, arrFreeQuantity, null, null);

						for (int i = 0; arrSaleQuantity != null && i < arrSaleQuantity.length; i++) {
							if (arrSaleQuantity[i] != null && !StringUtil.isNullOrEmpty(arrFreeProduct[i]) && arrFreeQuantity[i] != null) {
								GroupMua groupMua;
								GroupKM groupKM;
								unit = arrProductUnit[i];
								if (mapPromotionMua.get(promotionProgramCode) != null && mapPromotionKM.get(promotionProgramCode) != null) {
									groupMua = mapPromotionMua.get(promotionProgramCode).get(mapPromotionMua.get(promotionProgramCode).size() - 1);
									groupKM = mapPromotionKM.get(promotionProgramCode).get(mapPromotionKM.get(promotionProgramCode).size() - 1);
								} else {
									ListGroupMua lstGroupMua = new ListGroupMua();
									ListGroupKM lstGroupKM = new ListGroupKM();
									groupMua = new GroupMua();
									groupMua.groupCode = "N" + (lstGroupMua.size() + 1);
									//groupMua.groupCode = arrPromoGroupCode[i];
									groupMua.groupName = arrPromoGroupName[i];
									/*groupMua.multiple = arrMultiple[i];
									groupMua.recursive = arrRecursive[i];
									groupMua.dkgh = arrDkgh[i];*/
									
									groupMua.qttUnit = unit;
									groupKM = new GroupKM();
									groupKM.groupCode = "N" + (lstGroupKM.size() + 1);
									//groupKM.groupCode = arrPromoGroupCode[i];
									groupKM.groupName = arrPromoGroupName[i];
						/*			groupKM.multiple = arrMultiple[i];
									groupKM.recursive = arrRecursive[i];
									groupKM.dkgh = arrDkgh[i];*/
									
									groupKM.qttUnit = arrFreeProductUnit[i];
									groupMua.order = lstGroupMua.size() + 1;
									lstGroupMua.add(groupMua);
									groupKM.order = lstGroupKM.size() + 1;
									lstGroupKM.add(groupKM);
									mapPromotionMua.put(promotionProgramCode, lstGroupMua);
									mapPromotionKM.put(promotionProgramCode, lstGroupKM);
								}
								GroupSP groupSPMua = groupMua.add2Level(1, arrSaleQuantity[i], arrProductUnit[i], indexMua++, i, arrSaleQuantity, arrFreeProduct, arrFreeQuantity);
								if (groupSPMua == null) {
									groupMua = new GroupMua();
									List<GroupMua> lstGroupMua = mapPromotionMua.get(promotionProgramCode);
									lstGroupMua.add(groupMua);
									groupSPMua = groupMua.add2Level(1, arrSaleQuantity[i], null, indexMua++, i, arrSaleQuantity, arrFreeProduct, arrFreeQuantity);
								}
								List<Long> lstIndex = mapMuaKM.get(groupSPMua.index);//lay danh sach cac index muc duoc map voi muc sp mua
								List<GroupSP> lstLevelKM = groupKM.searchIndex(lstIndex);//lay danh sach cac muc duoc map voi muc sp mua
								GroupSP groupSPKM = groupKM.add2Level(mapPromotionTypeCheck.get(promotionProgramCode), arrFreeProduct[i], unit, arrFreeQuantity[i], arrAndOr[i], indexKM++, i, arrFreeProduct, arrFreeQuantity, lstLevelKM);
								if (groupSPKM == null) {
									groupKM = new GroupKM();
									List<GroupKM> lstGroupKM = mapPromotionKM.get(promotionProgramCode);
									lstGroupKM.add(groupKM);
									groupSPKM = groupKM.add2Level(mapPromotionTypeCheck.get(promotionProgramCode), arrFreeProduct[i], unit, arrFreeQuantity[i], arrAndOr[i], indexKM++, i, arrFreeProduct, arrFreeQuantity, null);
								}
								mapMuaKM.put(groupSPMua.index, groupSPKM.index);
							}
						}
					} else if (mapPromotionType.get(promotionProgramCode) == 11) {
						/**
						 * ZV23
						 */
						BigDecimal[] arrSaleQuantity = mapArraySaleQuantity.get(promotionProgramCode);
						Integer[] arrProductUnit = mapArrayQuantityUnit.get(promotionProgramCode);
						BigDecimal[] arrFreeAmount = mapArrayDiscountAmount.get(promotionProgramCode);
						sortQuantity(arrSaleQuantity, null, null, null, null, arrFreeAmount, null);
						for (int i = 0; arrSaleQuantity != null && i < arrSaleQuantity.length; i++) {
							if (arrSaleQuantity[i] != null && arrFreeAmount[i] != null) {
								GroupMua groupMua;
								GroupKM groupKM;
								unit = arrProductUnit[i];
								if (mapPromotionMua.get(promotionProgramCode) != null && mapPromotionKM.get(promotionProgramCode) != null) {
									groupMua = mapPromotionMua.get(promotionProgramCode).get(mapPromotionMua.get(promotionProgramCode).size() - 1);
									groupKM = mapPromotionKM.get(promotionProgramCode).get(mapPromotionKM.get(promotionProgramCode).size() - 1);
								} else {
									ListGroupMua lstGroupMua = new ListGroupMua();
									ListGroupKM lstGroupKM = new ListGroupKM();
									groupMua = new GroupMua();
									groupMua.groupCode = "N" + (lstGroupMua.size() + 1);
									//groupMua.groupCode = arrPromoGroupCode[i];
									groupMua.groupName = arrPromoGroupName[i];
								/*	groupMua.multiple = arrMultiple[i];
									groupMua.recursive = arrRecursive[i];
									groupMua.dkgh = arrDkgh[i];*/
									
									groupMua.qttUnit = unit;
									groupKM = new GroupKM();
									groupKM.groupCode = "N" + (lstGroupKM.size() + 1);
									//groupKM.groupCode = arrPromoGroupCode[i];
									groupKM.groupName = arrPromoGroupName[i];
							/*		groupKM.multiple = arrMultiple[i];
									groupKM.recursive = arrRecursive[i];
									groupKM.dkgh = arrDkgh[i];*/
									
									groupMua.order = lstGroupMua.size() + 1;
									lstGroupMua.add(groupMua);
									groupKM.order = lstGroupKM.size() + 1;
									lstGroupKM.add(groupKM);
									mapPromotionMua.put(promotionProgramCode, lstGroupMua);
									mapPromotionKM.put(promotionProgramCode, lstGroupKM);
								}
								GroupSP groupSPMua = groupMua.add2Level(1, arrSaleQuantity[i], arrProductUnit[i], indexMua++, i, arrSaleQuantity, null, null);
								if (groupSPMua == null) {
									groupMua = new GroupMua();
									List<GroupMua> lstGroupMua = mapPromotionMua.get(promotionProgramCode);
									lstGroupMua.add(groupMua);
									groupSPMua = groupMua.add2Level(1, arrSaleQuantity[i], arrProductUnit[i], indexMua++, i, arrSaleQuantity, null, null);
								}
								List<Long> lstIndex = mapMuaKM.get(groupSPMua.index);//lay danh sach cac index muc duoc map voi muc sp mua
								List<GroupSP> lstLevelKM = groupKM.searchIndex(lstIndex);//lay danh sach cac muc duoc map voi muc sp mua
								GroupSP groupSPKM = groupKM.add2Level(arrFreeAmount[i], indexKM++, i, arrFreeAmount, lstLevelKM);
								if (groupSPKM == null) {
									groupKM = new GroupKM();
									List<GroupKM> lstGroupKM = mapPromotionKM.get(promotionProgramCode);
									lstGroupKM.add(groupKM);
									groupSPKM = groupKM.add2Level(arrFreeAmount[i], indexKM++, i, arrFreeAmount, lstLevelKM);
								}
								mapMuaKM.put(groupSPMua.index, groupSPKM.index);
							}
						}
					} else if (mapPromotionType.get(promotionProgramCode) == 12) {
						/**
						 * 
						 */
						BigDecimal[] arrSaleQuantity = mapArraySaleQuantity.get(promotionProgramCode);
						Integer[] arrProductUnit = mapArrayQuantityUnit.get(promotionProgramCode);
						Float[] arrPercent = mapArrayDiscountPercent.get(promotionProgramCode);
						//Sort theo saleQuantity
						sortQuantity(arrSaleQuantity, null, null, null, null, null, arrPercent);
						for (int i = 0; arrSaleQuantity != null && i < arrSaleQuantity.length; i++) {
							if (arrSaleQuantity[i] != null && arrPercent[i] != null) {
								GroupMua groupMua;
								GroupKM groupKM;
								unit = arrProductUnit[i];
								if (mapPromotionMua.get(promotionProgramCode) != null && mapPromotionKM.get(promotionProgramCode) != null) {
									groupMua = mapPromotionMua.get(promotionProgramCode).get(mapPromotionMua.get(promotionProgramCode).size() - 1);
									groupKM = mapPromotionKM.get(promotionProgramCode).get(mapPromotionKM.get(promotionProgramCode).size() - 1);
								} else {
									ListGroupMua lstGroupMua = new ListGroupMua();
									ListGroupKM lstGroupKM = new ListGroupKM();
									groupMua = new GroupMua();
									groupMua.groupCode = "N" + (lstGroupMua.size() + 1);
									//groupMua.groupCode = arrPromoGroupCode[i];
									groupMua.groupName = arrPromoGroupName[i];
									/*groupMua.multiple = arrMultiple[i];
									groupMua.recursive = arrRecursive[i];
									groupMua.dkgh = arrDkgh[i];*/
									
									groupMua.qttUnit = unit;
									groupKM = new GroupKM();
									//groupKM.groupCode = "N" + (lstGroupKM.size() + 1);
									groupKM.groupCode = arrPromoGroupCode[i];
									groupKM.groupName = arrPromoGroupName[i];
									/*groupKM.multiple = arrMultiple[i];
									groupKM.recursive = arrRecursive[i];
									groupKM.dkgh = arrDkgh[i];*/
									
									groupMua.order = lstGroupMua.size() + 1;
									lstGroupMua.add(groupMua);
									groupKM.order = lstGroupKM.size() + 1;
									lstGroupKM.add(groupKM);
									mapPromotionMua.put(promotionProgramCode, lstGroupMua);
									mapPromotionKM.put(promotionProgramCode, lstGroupKM);
								}
								GroupSP groupSPMua = groupMua.add2Level(1, arrSaleQuantity[i], arrProductUnit[i], indexMua++, i, arrSaleQuantity, null, null);
								if (groupSPMua == null) {
									groupMua = new GroupMua();
									List<GroupMua> lstGroupMua = mapPromotionMua.get(promotionProgramCode);
									lstGroupMua.add(groupMua);
									groupSPMua = groupMua.add2Level(1, arrSaleQuantity[i], arrProductUnit[i], indexMua++, i, arrSaleQuantity, null, null);
								}
								List<Long> lstIndex = mapMuaKM.get(groupSPMua.index);//lay danh sach cac index muc duoc map voi muc sp mua
								List<GroupSP> lstLevelKM = groupKM.searchIndex(lstIndex);//lay danh sach cac muc duoc map voi muc sp mua
								GroupSP groupSPKM = groupKM.add2Level(arrPercent[i], indexKM++, i, arrPercent, lstLevelKM);
								if (groupSPKM == null) {
									groupKM = new GroupKM();
									List<GroupKM> lstGroupKM = mapPromotionKM.get(promotionProgramCode);
									lstGroupKM.add(groupKM);
									groupSPKM = groupKM.add2Level(arrPercent[i], indexKM++, i, arrPercent, lstLevelKM);
								}
								mapMuaKM.put(groupSPMua.index, groupSPKM.index);
							}
						}
					}
				}
				if (!StringUtil.isNullOrEmpty(messageError)) {
					errRow.setContent14(messageError);
					lstDetailError.add(errRow);
				}
				for (String promotionProgramCode : mapPromotionType.keySet()) {
					splitGroup(mapPromotionMua.get(promotionProgramCode), mapPromotionKM.get(promotionProgramCode), mapMuaKM, promotionProgramCode, lstProductPromo);
				}
			}
		}
	}

	/**
	 * Lay du lieu trong 1 o cell
	 * 
	 * @param cellData
	 *            - cell du lieu doc tu workbook
	 * 
	 * @return chuoi ki tu chua trong cell (khong null)
	 * 
	 * @author lacnv1
	 * @since Apr 13, 2015
	 */
	//	private String getCellValue(Cell cellData) throws Exception {
	//		if (cellData == null) {
	//			return "";
	//		}
	//		switch (cellData.getCellType()) {
	//		case Cell.CELL_TYPE_BLANK:
	//			return "";
	//		case Cell.CELL_TYPE_NUMERIC:
	//			if (HSSFDateUtil.isCellDateFormatted(cellData)) {
	//				return DateUtil.toDateString(cellData.getDateCellValue(), DateUtil.DATE_FORMAT_DDMMYYYY);
	//			} else {
	//				double val = cellData.getNumericCellValue();
	//				Double dVal = Double.valueOf(val);
	//				if (BigDecimal.valueOf(dVal).compareTo(BigDecimal.valueOf(dVal.longValue())) == 0) {
	//					return String.valueOf(BigDecimal.valueOf(dVal).longValue());
	//				} else {
	//					return String.valueOf(val);
	//				}
	//			}
	//		case Cell.CELL_TYPE_STRING:
	//			return cellData.getStringCellValue().trim();
	//		default:
	//			return cellData.toString();
	//		}
	//	}

	/**
	 * tach cac nhom san pham mua
	 * 
	 * @author tungmt
	 * @since 11/09/2014
	 */
	public void splitGroup(ListGroupMua lstGroupMua, ListGroupKM lstGroupKM, MapMuaKM mapMua, String promotionProgramCode, LinkedHashMap<String, Integer> lstProductPromo) {
		if (lstGroupMua != null && lstGroupKM != null) {
			for (int k = 0; k < lstGroupMua.size(); k++) {
				GroupMua groupMua = lstGroupMua.get(k);
				GroupKM groupKM = lstGroupKM.get(k);
				for (int j = 1; j < groupMua.lstLevel.size(); j++) {//so sanh khac nhau giua 2 level 
					GroupSP level1 = groupMua.lstLevel.get(0);
					GroupSP level2 = groupMua.lstLevel.get(j);
					GroupSP levelKM2 = groupKM.lstLevel.get(j);
					if (checkDif2Level(level1, level2)) {
						GroupMua newGroupMua;
						GroupKM newGroupKM;
						if (lstGroupMua.size() <= k + 1) {//chua co nhom tiep theo thi tao moi
							newGroupMua = new GroupMua();
							newGroupMua.groupCode = "N" + (lstGroupMua.size() + 1);
							newGroupMua.order = lstGroupMua.size() + 1;
							newGroupMua.qttUnit = level2.lstSP.get(0).quantityUnit;
							newGroupKM = new GroupKM();
							newGroupKM.groupCode = "N" + (lstGroupKM.size() + 1);
							newGroupKM.order = lstGroupKM.size() + 1;
							lstGroupMua.add(newGroupMua);
							lstGroupKM.add(newGroupKM);
						} else {
							newGroupMua = lstGroupMua.get(k + 1);
							newGroupKM = lstGroupKM.get(k + 1);
						}
						newGroupMua.lstLevel.add(new GroupSP(level2));
						newGroupKM.lstLevel.add(new GroupSP(levelKM2));
						groupMua.lstLevel.remove(j);
						groupKM.lstLevel.remove(j);
						j--;//lui lai 1 vi lstLevel da bi xoa 1 phan tu
					}
				}
			}
			//set lai order cho group
			// cac group phai co san pham khac nhau
			if (lstGroupMua != null && lstGroupMua.size() > 1) {
				//reset lai cac order
				for (GroupMua g : lstGroupMua) {
					g.order = 0;
				}
				int order = 1;
				for (Entry<String, Integer> entry : lstProductPromo.entrySet()) {
					String pp = entry.getKey();
					for (int i = 0; i < lstGroupMua.size(); i++) {
						if (lstGroupMua.get(i).order == 0) {
							List<Node> lstNode = lstGroupMua.get(i).lstLevel.get(0).lstSP;
							for (Node n : lstNode) {
								if (pp.toUpperCase().equals(promotionProgramCode + "-" + n.productCode)) {
									lstGroupMua.get(i).order = order;
									order++;
									break;
								}
							}
						}
					}
				}
				//truong hop co nhom chua set order thi mac dinh
				for (GroupMua g : lstGroupMua) {
					if (g.order == 0) {
						g.order = order;
						order++;
					}
				}
			}

			//set lai order cho grouplevel
			for (int k = 0; k < lstGroupMua.size(); k++) {
				GroupMua groupMua = lstGroupMua.get(k);
				GroupKM groupKM = lstGroupKM.get(k);
				int n = groupMua.lstLevel.size();
				for (int j = 0; j < groupMua.lstLevel.size(); j++) {//so sanh khac nhau giua 2 level 
					groupMua.lstLevel.get(j).order = n - j;
				}
				n = groupKM.lstLevel.size();
				for (int j = 0; j < groupKM.lstLevel.size(); j++) {//so sanh khac nhau giua 2 level 
					groupKM.lstLevel.get(j).order = n - j;
				}
			}

			//set va map mua va km
			mapPromotionMua.put(promotionProgramCode, lstGroupMua);
			mapPromotionKM.put(promotionProgramCode, lstGroupKM);
		}
	}

	/**
	 * neu 2 level khac san pham thi return true else false
	 * 
	 * @author tungmt
	 * @since 11/09/2014
	 */
	public boolean checkDif2Level(GroupSP g1, GroupSP g2) {
		for (Node n1 : g1.lstSP) {
			for (Node n2 : g2.lstSP) {
				if (!StringUtil.isNullOrEmpty(n1.productCode) && !StringUtil.isNullOrEmpty(n2.productCode) && n1.productCode.equals(n2.productCode)) {
					return false;
				} else if (StringUtil.isNullOrEmpty(n1.productCode) && StringUtil.isNullOrEmpty(n2.productCode)) {//type
					return false;
				}
			}
		}
		return true;
	}

	Integer isViewCustomerTab = 0;

	public Integer getIsViewCustomerTab() {
		return isViewCustomerTab;
	}

	public void setIsViewCustomerTab(Integer isViewCustomerTab) {
		this.isViewCustomerTab = isViewCustomerTab;
	}

	/**
	 * Xem thong tin CTKM
	 * 
	 * @author lacnv1
	 * @since Aug 21, 2014
	 */
	public String viewDetail() {
		actionStartTime = new Date();
		resetToken(result);
		try {
			if (currentUser.getStaffRoot() == null) {
				return PAGE_NOT_PERMISSION;
			}
	/*		private Boolean isDiscount;
			private Boolean isReward*/
			lstTypeCode = apParamMgr.getListApParam(ApParamType.PROMOTION, ActiveType.RUNNING);
			if (promotionId == null || promotionId == 0) {
				return SUCCESS;
			}
			
			promotionProgram = promotionProgramMgr.getPromotionProgramById(promotionId);
			if (promotionProgram == null) {
				isError = true;
				errMsg = ValidateUtil.getErrorMsg(ConstantManager.ERR_NOT_EXIST_DB, "CTKM");
				return SUCCESS;
			}
			//get promotion newcus config
			promotionNewcusConfig = promotionNewcusConfigMgr.getPromNewcusConfigByProgramId(promotionId);
			// kiem tra CTKM co het han hay khong
			Date sysDate = commonMgr.getSysDate();
			if (promotionProgram.getToDate() != null) {
				//flagStatusExpire
				if (DateUtil.compareDateWithoutTime(promotionProgram.getToDate(), sysDate) < 0) {
					promotionProgram.setFlagStatusExpire(ActiveType.HET_HAN.getValue());
				}
			}
			ApParam apListDist = apParamMgr.getApParamByCode("LIST_VISIBLE_DISCOUNT", ApParamType.ACTIVE_TYPE);
			String[] listDist;
			if(apListDist != null) {
				String strListDist = apListDist.getValue();
				strListDist = strListDist.replace(" ", "");
				listDist = strListDist.split(",");
			} else {
				String strZVDist = "ZV19,ZV20,ZV22,ZV23";
				String strZVDistRep = strZVDist.replace(" ", "");
				listDist = strZVDistRep.split(",");
			}
			
			ApParam apListRew = apParamMgr.getApParamByCode("LIST_VISIBLE_REWARD", ApParamType.ACTIVE_TYPE);
			String[] listRew;
			if(apListRew != null){
				String strListRew = apListRew.getValue();
				strListRew = strListRew.replace(" ", "");
				listRew = strListRew.split(",");
			} else {
				String strZV = "ZV01,ZV02,ZV04,ZV05,ZV07,ZV08,ZV10,ZV11,ZV13,ZV14,ZV16,ZV17,ZV19,ZV20,ZV22,ZV23";
				String strListZV = strZV.replace(" ", "");
				listRew = strListZV.split(",");
			}
			isReward = false;
			isDiscount = false;
			if(Arrays.asList(listDist).contains(promotionProgram.getType())){
//				if(promotionProgram != null 
//						&& (promotionProgram.getDiscountType() == null
//							|| promotionProgram.getDiscountType() == 0)){
					isDiscount = true;
//				}
				
			}else{
				isDiscount = false;
			}
			
			if(Arrays.asList(listRew).contains(promotionProgram.getType())){
				/*if(promotionProgram != null
						&& (promotionProgram.getRewardType() == null
							|| promotionProgram.getRewardType() == 0)){
					
				}*/
				isReward = true;
			}else{
				isReward = false;
			}
			
			
			
			ObjectVO<Product> listProductVO = productMgr.getListProduct(null, ActiveType.RUNNING);
			if (listProductVO != null) {
				listProduct = listProductVO.getLstObject();
			} else {
				listProduct = new ArrayList<Product>();
			}

			id = promotionId;
			if (promotionProgramMgr.checkExistPromotionShopMapByListShop(getStrListShopId(), promotionId)) {
				isViewCustomerTab = 1;
			}
			isShowCompleteDefinePromo = false;
			List<ApParam> lstSendEmail = apParamMgr.getListApParam(ApParamType.COMPLETE_DEFINE_PROMOTION, ActiveType.RUNNING);
			if (lstSendEmail != null && lstSendEmail.size() > 0) {
				for (int i = 0, n = lstSendEmail.size(); i < n; i++) {
					ApParam apParam = lstSendEmail.get(i);
					if (apParam != null && !StringUtil.isNullOrEmpty(apParam.getApParamCode())) {
						String[] arrApParamCode = apParam.getApParamCode().split(",");
						for (String string : arrApParamCode) {
							if (!StringUtil.isNullOrEmpty(string) && string.trim().equalsIgnoreCase(currentUser.getStaffRoot().getStaffCode())) {
								isShowCompleteDefinePromo = true;
								break;
							}
						}
					}
				}
			}
		} catch (Exception ex) {
			LogUtility.logErrorStandard(ex, R.getResource("web.log.message.error", "vnm.web.action.program.PromotionCatalogAction.viewDetail"), createLogErrorStandard(actionStartTime));
			result.put("errMsg", ValidateUtil.getErrorMsg(ConstantManager.ERR_SYSTEM));
			result.put(ERROR, true);
			return SUCCESS;
		}
		return SUCCESS;
	}

	/**
	 * 
	 * Xu ly hoan tat dinh nghia KM
	 * 
	 * @author trietptm
	 * @return String
	 * @since May 30, 2016
	 */
	public String completeDefinePromotion() {
		actionStartTime = new Date();
		resetToken(result);
		try {
			if (currentUser.getStaffRoot() == null) {
				return PAGE_NOT_PERMISSION;
			}
			if (promotionId != null && promotionId > 0) {
				promotionProgram = promotionProgramMgr.getPromotionProgramById(promotionId);
				if (promotionProgram == null) {
					result.put("errMsg", ValidateUtil.getErrorMsg(ConstantManager.ERR_NOT_EXIST_DB, Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "catalog.promotion")));
					result.put(ERROR, true);
					return SUCCESS;
				}
				String msgSendMail = super.sendEmailRecord(ActionSendMailType.COMPLETE_DEFINE_PROMOTION.getValue(), promotionProgram.getId(), currentUser.getStaffRoot().getStaffId());
				if (!SUCCESS.equals(msgSendMail)) {
					result.put("errMsg", msgSendMail);
				}
			}
		} catch (Exception ex) {
			LogUtility.logErrorStandard(ex, R.getResource("web.log.message.error", "vnm.web.action.program.PromotionCatalogAction.completeDefinePromotion"), createLogErrorStandard(actionStartTime));
			result.put("errMsg", ValidateUtil.getErrorMsg(ConstantManager.ERR_SYSTEM));
			result.put(ERROR, true);
			return SUCCESS;
		}
		return SUCCESS;
	}

	public String detailGroupProduct() {
		return SUCCESS;
	}

	public String addSaleProductGroup() {
		actionStartTime = new Date();
		resetToken(result);
		String errMsg = "";
		if ((StringUtil.isNullOrEmpty(groupCode) || StringUtil.isNullOrEmpty(groupName)) && maxQuantity == null && maxAmount == null) {
			errMsg = Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "common.user.input");
			result.put("errMsg", errMsg);
			result.put(ERROR, true);
			return SUCCESS;
		}
		try {
			//Them check XSS du lieu
			if (StringUtil.isNullOrEmpty(errMsg) && !StringUtil.isNullOrEmpty(groupCode)) {
				errMsg = ValidateUtil.getErrorMsgQuickly(ConstantManager.ERR_EXIST_SPECIAL_CHAR_IN_SPECIAL, null);
			}
			if (StringUtil.isNullOrEmpty(errMsg) && !StringUtil.isNullOrEmpty(groupName)) {
				errMsg = ValidateUtil.getErrorMsgQuickly(ConstantManager.ERR_EXIST_SPECIAL_CHAR_IN_SPECIAL, null);
			}
			if (!StringUtil.isNullOrEmpty(errMsg)) {
				result.put("errMsg", errMsg);
				result.put(ERROR, true);
				return SUCCESS;
			}
			if (groupId != null && groupId > 0) {
				ProductGroup productGroup = promotionProgramMgr.getProductGroupById(groupId);
				if (productGroup == null) {
					errMsg = ValidateUtil.getErrorMsg(ConstantManager.ERR_SYSTEM);
					result.put("errMsg", errMsg);
					result.put(ERROR, true);
					return SUCCESS;
				}
				productGroup.setProductGroupName(groupName);
				productGroup.setMinQuantity(minQuantity);
				productGroup.setMinAmount(minAmount);
				productGroup.setMultiple(multiple != null && multiple ? 1 : 0);
				productGroup.setRecursive(recursive != null && recursive ? 1 : 0);
				productGroup.setOrder(stt);
				promotionProgramMgr.updateProductGroup(productGroup, getLogInfoVO());
				if (productGroup != null && productGroup.getPromotionProgram() != null) {
					promotionProgramMgr.updateMD5ValidCode(productGroup.getPromotionProgram(), getLogInfoVO());
				}
			} else {
				ProductGroup productGroup = promotionProgramMgr.getProductGroupByCode(groupCode, ProductGroupType.MUA, promotionId);
				if (productGroup != null) {
					errMsg = ValidateUtil.getErrorMsg(ConstantManager.ERR_EXIST, groupCode);
					result.put("errMsg", errMsg);
					result.put(ERROR, true);
					return SUCCESS;
				}
				productGroup = promotionProgramMgr.createProductGroup(promotionId, groupCode, groupName, ProductGroupType.MUA, minQuantity, maxQuantity, minAmount, maxAmount, multiple, recursive, stt, getLogInfoVO());
				if (productGroup != null && productGroup.getPromotionProgram() != null) {
					promotionProgramMgr.updateMD5ValidCode(productGroup.getPromotionProgram(), getLogInfoVO());
				}
			}
		} catch (Exception e) {
			LogUtility.logErrorStandard(e, R.getResource("web.log.message.error", "vnm.web.action.program.PromotionCatalogAction.addSaleProductGroup"), createLogErrorStandard(actionStartTime));
			result.put("errMsg", ValidateUtil.getErrorMsg(ConstantManager.ERR_SYSTEM));
			result.put(ERROR, true);
			return SUCCESS;
		}
		return SUCCESS;
	}

	/**
	 * Them thong tin nhom
	 * 
	 * @return
	 * @since 08-09-2015
	 * @description cap nhat code
	 */
	public String addNewProductGroup() {
		actionStartTime = new Date();
		resetToken(result);
		if ((StringUtil.isNullOrEmpty(groupCode) || StringUtil.isNullOrEmpty(groupName)) && maxQuantity == null && maxAmount == null) {
			errMsg = Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "common.user.input");
			result.put("errMsg", errMsg);
			result.put(ERROR, true);
			return SUCCESS;
		}
		errMsg = ValidateUtil.validateField(groupCode, "catalog.promotion.group.code", 100, ConstantManager.ERR_REQUIRE, ConstantManager.ERR_EXIST_SPECIAL_CHAR_IN_CODE, ConstantManager.ERR_MAX_LENGTH);
		if (StringUtil.isNullOrEmpty(errMsg)) {
			errMsg = ValidateUtil.validateField(groupName, "catalog.promotion.group.name", 200, ConstantManager.ERR_REQUIRE, ConstantManager.ERR_EXIST_SPECIAL_CHAR_IN_SPECIAL, ConstantManager.ERR_MAX_LENGTH);
		}
		if (!StringUtil.isNullOrEmpty(errMsg)) {
			result.put("errMsg", errMsg);
			result.put(ERROR, true);
			return SUCCESS;
		}
		try {
			if (groupMuaId != null && groupMuaId > 0 && groupKMId != null && groupKMId > 0) {
				ProductGroup productGroupMua = promotionProgramMgr.getProductGroupById(groupMuaId);
				ProductGroup productGroupKM = promotionProgramMgr.getProductGroupById(groupKMId);
				if (productGroupMua == null) {
					errMsg = ValidateUtil.getErrorMsg(ConstantManager.ERR_SYSTEM);
					result.put("errMsg", errMsg);
					result.put(ERROR, true);
					return SUCCESS;
				}
				if (productGroupKM == null) {
					errMsg = ValidateUtil.getErrorMsg(ConstantManager.ERR_SYSTEM);
					result.put("errMsg", errMsg);
					result.put(ERROR, true);
					return SUCCESS;
				}
				productGroupMua.setProductGroupName(groupName);
				productGroupMua.setMinQuantity(minQuantity);
				productGroupMua.setMinAmount(minAmount);
				productGroupMua.setMultiple(multiple != null && multiple ? 1 : 0);
				productGroupMua.setRecursive(recursive != null && recursive ? 1 : 0);
				productGroupMua.setOrder(stt);
				productGroupMua.setQuantityUnit(quantityUnit);
				promotionProgramMgr.updateProductGroup(productGroupMua, getLogInfoVO());
				productGroupKM.setProductGroupName(groupName);
				productGroupKM.setMaxQuantity(maxQuantity);
				productGroupKM.setMaxAmount(maxAmount);
				productGroupKM.setMultiple(multiple != null && multiple ? 1 : 0);
				productGroupKM.setRecursive(recursive != null && recursive ? 1 : 0);
				productGroupKM.setOrder(stt);
				productGroupKM.setQuantityUnit(quantityUnit);
				promotionProgramMgr.updateProductGroup(productGroupKM, getLogInfoVO());
				if (productGroupMua != null && productGroupMua.getPromotionProgram() != null) {
					promotionProgramMgr.updateMD5ValidCode(productGroupMua.getPromotionProgram(), getLogInfoVO());
				}
			} else {
				final List<String> lstZVMultiGroup = Arrays.asList(PromotionType.ZV01.getValue(), PromotionType.ZV02.getValue(), PromotionType.ZV03.getValue(), PromotionType.ZV04.getValue(), PromotionType.ZV05.getValue(), PromotionType.ZV06
						.getValue(), PromotionType.ZV09.getValue(), PromotionType.ZV21.getValue()
				//						PromotionType.ZV25.getValue(),
				//						PromotionType.ZV26.getValue(),
				//						PromotionType.ZV27.getValue()
				);
				PromotionProgram pp = promotionProgramMgr.getPromotionProgramById(promotionId);
				if (pp == null) {
					result.put(ERROR, true);
					result.put("errMsg", ValidateUtil.getErrorMsg(ConstantManager.ERR_NOT_EXIST_DB, promotionId));
					return SUCCESS;
				}
				String type = pp.getType();
				if (!lstZVMultiGroup.contains(type)) {
					List<ProductGroup> lstGroupTmp = promotionProgramMgr.getListProductGroupByPromotionId(promotionId, ProductGroupType.MUA);
					if (lstGroupTmp != null && lstGroupTmp.size() > 0) {
						errMsg = R.getResource("promotion.program.multi.group.invalid");
						result.put("errMsg", errMsg);
						result.put(ERROR, true);
						return SUCCESS;
					}
				}
				ProductGroup productGroupMua = promotionProgramMgr.getProductGroupByCode(groupCode, ProductGroupType.MUA, promotionId);
				ProductGroup productGroupKM = promotionProgramMgr.getProductGroupByCode(groupCode, ProductGroupType.KM, promotionId);
				if (productGroupMua != null) {
					errMsg = ValidateUtil.getErrorMsg(ConstantManager.ERR_EXIST, groupCode);
					result.put("errMsg", errMsg);
					result.put(ERROR, true);
					return SUCCESS;
				}
				if (productGroupKM != null) {
					errMsg = ValidateUtil.getErrorMsg(ConstantManager.ERR_EXIST, groupCode);
					result.put("errMsg", errMsg);
					result.put(ERROR, true);
					return SUCCESS;
				}
				//productGroupMua = promotionProgramMgr.createProductGroup(promotionId, groupCode, groupName, ProductGroupType.MUA, minQuantity, null, minAmount, null, multiple, recursive, stt, getLogInfoVO());
				//productGroupKM = promotionProgramMgr.createProductGroup(promotionId, groupCode, groupName, ProductGroupType.KM, null, maxQuantity, null, maxAmount, multiple, recursive, stt, getLogInfoVO());
				productGroupMua = new ProductGroup();
				productGroupMua.setProductGroupCode(groupCode);
				productGroupMua.setProductGroupName(groupName);
				productGroupMua.setPromotionProgram(pp);
				productGroupMua.setGroupType(ProductGroupType.MUA);
				productGroupMua.setMinQuantity(minQuantity);
				productGroupMua.setMinAmount(minAmount);
				productGroupMua.setMaxQuantity(null);
				productGroupMua.setMaxAmount(null);
				productGroupMua.setMultiple((multiple != null && multiple == true) ? 1 : 0);
				productGroupMua.setRecursive((recursive != null && recursive == true) ? 1 : 0);
				productGroupMua.setOrder(stt);
				productGroupMua.setQuantityUnit(quantityUnit);
				productGroupMua = promotionProgramMgr.createProductGroup(productGroupMua, getLogInfoVO());

				productGroupKM = new ProductGroup();
				productGroupKM.setProductGroupCode(groupCode);
				productGroupKM.setProductGroupName(groupName);
				productGroupKM.setPromotionProgram(pp);
				productGroupKM.setGroupType(ProductGroupType.KM);
				productGroupKM.setMinQuantity(minQuantity);
				productGroupKM.setMinAmount(minAmount);
				productGroupKM.setMaxQuantity(null);
				productGroupKM.setMaxAmount(null);
				productGroupKM.setMultiple((multiple != null && multiple == true) ? 1 : 0);
				productGroupKM.setRecursive((recursive != null && recursive == true) ? 1 : 0);
				productGroupKM.setOrder(stt);
				productGroupKM.setQuantityUnit(quantityUnit);
				promotionProgramMgr.createProductGroup(productGroupKM, getLogInfoVO());
				if (productGroupMua != null && productGroupMua.getPromotionProgram() != null) {
					promotionProgramMgr.updateMD5ValidCode(productGroupMua.getPromotionProgram(), getLogInfoVO());
				}
			}
		} catch (Exception e) {
			LogUtility.logErrorStandard(e, R.getResource("web.log.message.error", "vnm.web.action.program.PromotionCatalogAction.addNewProductGroup"), createLogErrorStandard(actionStartTime));
			result.put("errMsg", ValidateUtil.getErrorMsg(ConstantManager.ERR_SYSTEM));
			result.put(ERROR, true);
			return SUCCESS;
		}
		return SUCCESS;
	}

	/**
	 * @modify hunglm16
	 * @return
	 * @since October 09, 2015
	 * @description bo sung kiem tra ATTT XSS
	 */
	public String updateSaleProductGroup() {
		actionStartTime = new Date();
		resetToken(result);
		try {
			errMsg = "";
			if (groupId != null && groupId > 0) {
				ProductGroup productGroup = promotionProgramMgr.getProductGroupById(groupId);
				if (productGroup == null) {
					errMsg = ValidateUtil.getErrorMsg(ConstantManager.ERR_SYSTEM);
				}
				//Them check XSS du lieu
				if (StringUtil.isNullOrEmpty(errMsg) && !StringUtil.isNullOrEmpty(groupName)) {
					errMsg = ValidateUtil.getErrorMsgQuickly(ConstantManager.ERR_EXIST_SPECIAL_CHAR_IN_SPECIAL, null);
				}
				if (productGroup == null || !StringUtil.isNullOrEmpty(errMsg)) {
					result.put("errMsg", errMsg);
					result.put(ERROR, true);
					return SUCCESS;
				}
				productGroup.setProductGroupName(groupName);
				productGroup.setMinQuantity(minQuantity);
				productGroup.setMinAmount(minAmount);
				productGroup.setMultiple(multiple != null && multiple ? 1 : 0);
				productGroup.setRecursive(recursive != null && recursive ? 1 : 0);
				productGroup.setOrder(stt);
				if (listMinQuantity == null || listMinQuantity.isEmpty() || listMinAmount == null || listMinAmount.isEmpty() || listOrder == null || listOrder.isEmpty()) {
					promotionProgramMgr.updateProductGroup(productGroup, getLogInfoVO());
					if (productGroup.getPromotionProgram() != null) {
						promotionProgramMgr.updateMD5ValidCode(productGroup.getPromotionProgram(), getLogInfoVO());
					}
					return SUCCESS;
				}
				promotionProgramMgr.addLevel2SaleProductGroup(productGroup, listLevelId, listMinQuantity, listMinAmount, listOrder, listProductDetail, getLogInfoVO());
				if (productGroup.getPromotionProgram() != null) {
					promotionProgramMgr.updateMD5ValidCode(productGroup.getPromotionProgram(), getLogInfoVO());
				}
			}
		} catch (Exception e) {
			LogUtility.logErrorStandard(e, R.getResource("web.log.message.error", "vnm.web.action.program.PromotionCatalogAction.updateSaleProductGroup"), createLogErrorStandard(actionStartTime));
			errMsg = ValidateUtil.getErrorMsg(ConstantManager.ERR_SYSTEM);
			result.put("errMsg", errMsg);
			result.put(ERROR, true);
			return SUCCESS;
		}
		return SUCCESS;
	}

	public String addFreeProductGroup() {
		actionStartTime = new Date();
		resetToken(result);
		errMsg = "";
		if ((StringUtil.isNullOrEmpty(groupCode) || StringUtil.isNullOrEmpty(groupName)) && maxQuantity == null && maxAmount == null) {
			errMsg = Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "common.user.input");
			result.put("errMsg", errMsg);
			result.put(ERROR, true);
			return SUCCESS;
		}
		try {
			if (groupId != null && groupId > 0) {
				ProductGroup productGroup = promotionProgramMgr.getProductGroupById(groupId);
				if (productGroup == null) {
					errMsg = ValidateUtil.getErrorMsg(ConstantManager.ERR_SYSTEM);
					result.put("errMsg", errMsg);
					result.put(ERROR, true);
					return SUCCESS;
				}
				//Them check XSS du lieu
				if (StringUtil.isNullOrEmpty(errMsg) && !StringUtil.isNullOrEmpty(groupName)) {
					errMsg = ValidateUtil.getErrorMsgQuickly(ConstantManager.ERR_EXIST_SPECIAL_CHAR_IN_SPECIAL, null);
				}
				if (!StringUtil.isNullOrEmpty(errMsg)) {
					result.put("errMsg", errMsg);
					result.put(ERROR, true);
					return SUCCESS;
				}
				productGroup.setProductGroupName(groupName);
				productGroup.setMaxQuantity(maxQuantity);
				productGroup.setMaxAmount(maxAmount);
				promotionProgramMgr.updateProductGroup(productGroup, getLogInfoVO());
				if (productGroup.getPromotionProgram() != null) {
					promotionProgramMgr.updateMD5ValidCode(productGroup.getPromotionProgram(), getLogInfoVO());
				}
			} else {
				ProductGroup productGroup = promotionProgramMgr.getProductGroupByCode(groupCode, ProductGroupType.KM, promotionId);
				if (productGroup != null) {
					errMsg = ValidateUtil.getErrorMsg(ConstantManager.ERR_EXIST, groupCode);
					result.put("errMsg", errMsg);
					result.put(ERROR, true);
					return SUCCESS;
				}
				//Them check XSS du lieu
				if (StringUtil.isNullOrEmpty(errMsg) && !StringUtil.isNullOrEmpty(groupCode)) {
					errMsg = ValidateUtil.getErrorMsgQuickly(ConstantManager.ERR_EXIST_SPECIAL_CHAR_IN_SPECIAL, null);
				}
				if (StringUtil.isNullOrEmpty(errMsg) && !StringUtil.isNullOrEmpty(groupName)) {
					errMsg = ValidateUtil.getErrorMsgQuickly(ConstantManager.ERR_EXIST_SPECIAL_CHAR_IN_SPECIAL, null);
				}
				if (!StringUtil.isNullOrEmpty(errMsg)) {
					result.put("errMsg", errMsg);
					result.put(ERROR, true);
					return SUCCESS;
				}
				productGroup = promotionProgramMgr.createProductGroup(promotionId, groupCode, groupName, ProductGroupType.KM, minQuantity, maxQuantity, minAmount, maxAmount, multiple, recursive, stt, getLogInfoVO());
				if (productGroup != null && productGroup.getPromotionProgram() != null) {
					promotionProgramMgr.updateMD5ValidCode(productGroup.getPromotionProgram(), getLogInfoVO());
				}
			}
		} catch (Exception e) {
			LogUtility.logErrorStandard(e, R.getResource("web.log.message.error", "vnm.web.action.program.PromotionCatalogAction.addFreeProductGroup"), createLogErrorStandard(actionStartTime));
			errMsg = ValidateUtil.getErrorMsg(ConstantManager.ERR_SYSTEM);
			result.put("errMsg", errMsg);
			result.put(ERROR, true);
			return SUCCESS;
		}
		return SUCCESS;
	}

	public String updateFreeProductGroup() {
		actionStartTime = new Date();
		resetToken(result);
		errMsg = "";
		try {
			if (groupId != null && groupId > 0) {
				ProductGroup productGroup = promotionProgramMgr.getProductGroupById(groupId);
				if (productGroup == null) {
					errMsg = ValidateUtil.getErrorMsg(ConstantManager.ERR_SYSTEM);
					result.put("errMsg", errMsg);
					result.put(ERROR, true);
					return SUCCESS;
				}
				//Them check XSS du lieu
				if (StringUtil.isNullOrEmpty(errMsg) && !StringUtil.isNullOrEmpty(groupName)) {
					errMsg = ValidateUtil.getErrorMsgQuickly(ConstantManager.ERR_EXIST_SPECIAL_CHAR_IN_SPECIAL, null);
				}
				if (!StringUtil.isNullOrEmpty(errMsg)) {
					result.put("errMsg", errMsg);
					result.put(ERROR, true);
					return SUCCESS;
				}
				productGroup.setProductGroupName(groupName);
				productGroup.setMinQuantity(new BigDecimal(maxQuantity));
				productGroup.setMinAmount(maxAmount);
				if (listMaxQuantity == null || listMaxQuantity.isEmpty() || listMaxAmount == null || listMaxAmount.isEmpty() || listOrder == null || listOrder.isEmpty() || listPercent == null || listPercent.isEmpty()) {
					promotionProgramMgr.updateProductGroup(productGroup, getLogInfoVO());
				} else {
					promotionProgramMgr.addLevel2FreeProductGroup(productGroup, listLevelId, listMaxQuantity, listOrder, listMaxAmount, listPercent, listProductDetail, getLogInfoVO());
				}
				if (productGroup.getPromotionProgram() != null) {
					promotionProgramMgr.updateMD5ValidCode(productGroup.getPromotionProgram(), getLogInfoVO());
				}
			}
		} catch (Exception e) {
			LogUtility.logErrorStandard(e, R.getResource("web.log.message.error", "vnm.web.action.program.PromotionCatalogAction.updateFreeProductGroup"), createLogErrorStandard(actionStartTime));
			errMsg = ValidateUtil.getErrorMsg(ConstantManager.ERR_SYSTEM);
			result.put("errMsg", errMsg);
			result.put(ERROR, true);
			return SUCCESS;
		}
		return SUCCESS;
	}

	public String deleteProductGroup() {
		actionStartTime = new Date();
		resetToken(result);
		try {
			ProductGroup productGroup = promotionProgramMgr.getProductGroupById(groupId);
			promotionProgramMgr.deleteProductGroup(productGroup, getLogInfoVO());
			if (productGroup != null && productGroup.getPromotionProgram() != null) {
				promotionProgramMgr.updateMD5ValidCode(productGroup.getPromotionProgram(), getLogInfoVO());
			}
		} catch (Exception e) {
			LogUtility.logErrorStandard(e, R.getResource("web.log.message.error", "vnm.web.action.program.PromotionCatalogAction.deleteProductGroup"), createLogErrorStandard(actionStartTime));
			errMsg = ValidateUtil.getErrorMsg(ConstantManager.ERR_SYSTEM);
			result.put("errMsg", errMsg);
			result.put(ERROR, true);
			return SUCCESS;
		}
		return SUCCESS;
	}

	/**
	 * Xoa nhom san pham km
	 * 
	 * @modify hunglm16
	 * @return
	 * @description Update Co cau
	 */
	public String deleteProductGroupNew() {
		actionStartTime = new Date();
		resetToken(result);
		try {
			ProductGroup productGroupMua = promotionProgramMgr.getProductGroupById(groupMuaId);
			ProductGroup productGroupKM = promotionProgramMgr.getProductGroupById(groupKMId);
			promotionProgramMgr.deleteProductGroup(productGroupMua, getLogInfoVO());
			promotionProgramMgr.deleteProductGroup(productGroupKM, getLogInfoVO());
			if (productGroupMua != null && productGroupMua.getPromotionProgram() != null) {
				promotionProgramMgr.updateMD5ValidCode(productGroupMua.getPromotionProgram(), getLogInfoVO());
			}
		} catch (Exception e) {
			LogUtility.logErrorStandard(e, R.getResource("web.log.message.error", "vnm.web.action.program.PromotionCatalogAction.deleteProductGroupNew"), createLogErrorStandard(actionStartTime));
			errMsg = ValidateUtil.getErrorMsg(ConstantManager.ERR_SYSTEM);
			result.put("errMsg", errMsg);
			result.put(ERROR, true);
			return SUCCESS;
		}
		return SUCCESS;
	}

	public String groupSaleProduct() {
		actionStartTime = new Date();
		try {
			lstGroupSale = promotionProgramMgr.getListProductGroupByPromotionId(id, ProductGroupType.MUA);
		} catch (Exception e) {
			lstGroupSale = new ArrayList<ProductGroup>();
			LogUtility.logErrorStandard(e, R.getResource("web.log.message.error", "vnm.web.action.program.PromotionCatalogAction.groupSaleProduct"), createLogErrorStandard(actionStartTime));
			errMsg = ValidateUtil.getErrorMsg(ConstantManager.ERR_SYSTEM);
			result.put("errMsg", errMsg);
			result.put(ERROR, true);
			return SUCCESS;

		}
		return SUCCESS;
	}

	public String groupFreeProduct() {
		actionStartTime = new Date();
		try {
			lstGroupFree = promotionProgramMgr.getListProductGroupByPromotionId(id, ProductGroupType.KM);
		} catch (Exception e) {
			lstGroupSale = new ArrayList<ProductGroup>();
			LogUtility.logErrorStandard(e, R.getResource("web.log.message.error", "vnm.web.action.program.PromotionCatalogAction.groupFreeProduct"), createLogErrorStandard(actionStartTime));
			errMsg = ValidateUtil.getErrorMsg(ConstantManager.ERR_SYSTEM);
			result.put("errMsg", errMsg);
			result.put(ERROR, true);
			return SUCCESS;
		}
		return SUCCESS;
	}

	public String groupNewProduct() {
		actionStartTime = new Date();
		try {
			lstGroupNew = promotionProgramMgr.getListNewProductGroupByPromotionId(id);
		} catch (Exception e) {
			lstGroupNew = new ArrayList<NewProductGroupVO>();
			LogUtility.logErrorStandard(e, R.getResource("web.log.message.error", "vnm.web.action.program.PromotionCatalogAction.groupNewProduct"), createLogErrorStandard(actionStartTime));
			errMsg = ValidateUtil.getErrorMsg(ConstantManager.ERR_SYSTEM);
			result.put("errMsg", errMsg);
			result.put(ERROR, true);
			return SUCCESS;
		}
		return SUCCESS;
	}

	public String listLevel() {
		actionStartTime = new Date();
		try {
			lstLevel = promotionProgramMgr.getListGroupLevelVO(groupId);
		} catch (Exception e) {
			lstLevel = new ArrayList<GroupLevelVO>();
			LogUtility.logErrorStandard(e, R.getResource("web.log.message.error", "vnm.web.action.program.PromotionCatalogAction.listLevel"), createLogErrorStandard(actionStartTime));
			errMsg = ValidateUtil.getErrorMsg(ConstantManager.ERR_SYSTEM);
			result.put("errMsg", errMsg);
			result.put(ERROR, true);
			return SUCCESS;
		}
		return SUCCESS;
	}

	public String deleteMuaLevel() {
		actionStartTime = new Date();
		resetToken(result);
		try {
			GroupLevel level = promotionProgramMgr.getGroupLevelByLevelId(levelMuaId);
			if (level == null) {
				result.put(ERROR, true);
				result.put("errMsg", Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "promotion.group.level.not.exist"));
				return SUCCESS;
			}
			List<GroupMapping> listMapping = promotionProgramMgr.getListGroupMappingByLevelId(levelMuaId, levelKMId);
			if (!listMapping.isEmpty()) {
				result.put(ERROR, true);
				result.put("errMsg", Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "promotion.group.level.mapping.exist"));
				return SUCCESS;
			}

			promotionProgramMgr.deleteGroupLevel(levelMuaId, getLogInfoVO());
			if (level.getProductGroup() != null && level.getProductGroup().getPromotionProgram() != null) {
				promotionProgramMgr.updateMD5ValidCode(level.getProductGroup().getPromotionProgram(), getLogInfoVO());
			}
			result.put(ERROR, false);
		} catch (Exception e) {
			LogUtility.logErrorStandard(e, R.getResource("web.log.message.error", "vnm.web.action.program.PromotionCatalogAction.deleteMuaLevel"), createLogErrorStandard(actionStartTime));
			errMsg = ValidateUtil.getErrorMsg(ConstantManager.ERR_SYSTEM);
			result.put("errMsg", errMsg);
			result.put(ERROR, true);
			return SUCCESS;
		}
		return SUCCESS;
	}

	public String deleteKMLevel() {
		actionStartTime = new Date();
		resetToken(result);
		try {
			GroupLevel level = promotionProgramMgr.getGroupLevelByLevelId(levelKMId);
			if (level == null) {
				result.put(ERROR, true);
				result.put("errMsg", Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "promotion.group.level.not.exist"));
				return SUCCESS;
			}
			List<GroupMapping> listMapping = promotionProgramMgr.getListGroupMappingByLevelId(levelMuaId, levelKMId);
			if (!listMapping.isEmpty()) {
				result.put(ERROR, true);
				result.put("errMsg", Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "promotion.group.level.mapping.exist"));
				return SUCCESS;
			}

			promotionProgramMgr.deleteGroupLevel(levelKMId, getLogInfoVO());
			if (level.getProductGroup() != null && level.getProductGroup().getPromotionProgram() != null) {
				promotionProgramMgr.updateMD5ValidCode(level.getProductGroup().getPromotionProgram(), getLogInfoVO());
			}
			result.put(ERROR, false);
		} catch (Exception e) {
			LogUtility.logErrorStandard(e, R.getResource("web.log.message.error", "vnm.web.action.program.PromotionCatalogAction.deleteKMLevel"), createLogErrorStandard(actionStartTime));
			errMsg = ValidateUtil.getErrorMsg(ConstantManager.ERR_SYSTEM);
			result.put("errMsg", errMsg);
			result.put(ERROR, true);
			return SUCCESS;
		}
		return SUCCESS;
	}

	public String allGroupOfPromotion() {
		actionStartTime = new Date();
		try {
			List<ProductGroupVO> lstGroupSaleVO = promotionProgramMgr.getListProductGroupVO(id, ProductGroupType.MUA);
			List<ProductGroupVO> lstGroupFreeVO = promotionProgramMgr.getListProductGroupVO(id, ProductGroupType.KM);
			result.put("lstGroupSale", lstGroupSaleVO);
			result.put("lstGroupFree", lstGroupFreeVO);
			result.put(ERROR, false);
		} catch (Exception e) {
			LogUtility.logErrorStandard(e, R.getResource("web.log.message.error", "vnm.web.action.program.PromotionCatalogAction.allGroupOfPromotion"), createLogErrorStandard(actionStartTime));
			errMsg = ValidateUtil.getErrorMsg(ConstantManager.ERR_SYSTEM);
			result.put("errMsg", errMsg);
			result.put(ERROR, true);
			return SUCCESS;
		}
		return SUCCESS;
	}

	public String listLevelOfGroup() {
		actionStartTime = new Date();
		try {
			List<GroupLevelVO> listLevelMua = promotionProgramMgr.getListGroupLevelVO(groupMuaId);
			List<GroupLevelVO> listLevelKM = promotionProgramMgr.getListGroupLevelVO(groupKMId);
			result.put("listLevelMua", listLevelMua);
			result.put("listLevelKM", listLevelKM);
			result.put(ERROR, false);
		} catch (Exception e) {
			LogUtility.logErrorStandard(e, R.getResource("web.log.message.error", "vnm.web.action.program.PromotionCatalogAction.listLevelOfGroup"), createLogErrorStandard(actionStartTime));
			errMsg = ValidateUtil.getErrorMsg(ConstantManager.ERR_SYSTEM);
			result.put("errMsg", errMsg);
			result.put(ERROR, true);
			return SUCCESS;
		}
		return SUCCESS;
	}

	public String listMapping() {
		actionStartTime = new Date();
		try {
			/*
			 * if(groupMuaId == null || groupMuaId <= 0 || groupKMId == null ||
			 * groupKMId <= 0) { listLevelMapping = new
			 * ArrayList<LevelMappingVO>(); return SUCCESS; }
			 */
			listLevelMapping = promotionProgramMgr.getListLevelMappingByGroupId(id, groupMuaId, groupKMId);
		} catch (Exception e) {
			listLevelMapping = new ArrayList<LevelMappingVO>();
			LogUtility.logErrorStandard(e, R.getResource("web.log.message.error", "vnm.web.action.program.PromotionCatalogAction.listMapping"), createLogErrorStandard(actionStartTime));
			result.put(ERROR, true);
			result.put("errMsg", ValidateUtil.getErrorMsg(ConstantManager.ERR_SYSTEM));
		}
		return SUCCESS;
	}

	public String deleteMapping() {
		resetToken(result);
		try {
			GroupMapping groupMapping = null;
			if (mappingId != null) {
				groupMapping = promotionProgramMgr.getGroupMappingById(mappingId);
				if (groupMapping != null) {
					promotionProgramMgr.deleteGroupMapping(groupMapping);
				} else {
					result.put(ERROR, true);
					result.put("errMsg", ValidateUtil.getErrorMsg(ConstantManager.ERR_SYSTEM));
					return SUCCESS;
				}
			} else if (!StringUtil.isNullOrEmpty(groupMuaCode) && !StringUtil.isNullOrEmpty(groupKMCode) && orderLevelMua != null && orderLevelKM != null) {
				groupMapping = promotionProgramMgr.getGroupMappingByGroupCodeAndOrder(id, groupMuaCode, orderLevelMua, groupKMCode, orderLevelKM);
				if (groupMapping != null) {
					promotionProgramMgr.deleteGroupMapping(groupMapping);
				} else {
					result.put(ERROR, true);
					result.put("errMsg", Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "promotion.group.mapping.doesnt.exist", groupMuaCode, orderLevelMua, groupKMCode, orderLevelKM));
					return SUCCESS;
				}
			}
			if (groupMapping != null && groupMapping.getPromotionGroup() != null && groupMapping.getPromotionGroup().getPromotionProgram() != null) {
				promotionProgramMgr.updateMD5ValidCode(groupMapping.getPromotionGroup().getPromotionProgram(), getLogInfoVO());
			}
			result.put(ERROR, false);
		} catch (Exception e) {
			result.put(ERROR, true);
			result.put("errMsg", ValidateUtil.getErrorMsg(ConstantManager.ERR_SYSTEM));
			LogUtility.logError(e, e.getMessage());
		}
		return SUCCESS;
	}

	public String saveMapping() {
		try {
			List<GroupMapping> listExisted = promotionProgramMgr.getGroupMappingByGroupCodeAndOrderMua(id, groupMuaCode, orderLevelMua);
			if (!listExisted.isEmpty()) {
				result.put(ERROR, true);
				result.put("errMsg", Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "promotion.group.mapping.existed", groupMuaCode, orderLevelMua));
				return SUCCESS;
			}
			GroupMapping groupMapping = promotionProgramMgr.createGroupMappingByGroupCodeAndOrder(id, groupMuaCode, orderLevelMua, groupKMCode, orderLevelKM, getLogInfoVO());
			LevelMappingVO __groupMapping = new LevelMappingVO();
			if (groupMapping != null) {
				__groupMapping.setMappingId(groupMapping.getId());
				if (groupMapping.getSaleGroupLevel() != null) {
					__groupMapping.setIdLevelMua(groupMapping.getSaleGroupLevel().getId());
				}
				__groupMapping.setGroupMuaCode(groupMapping.getSaleGroup() != null ? groupMapping.getSaleGroup().getProductGroupCode() : null);
				__groupMapping.setGroupMuaName(groupMapping.getSaleGroup() != null ? groupMapping.getSaleGroup().getProductGroupName() : null);
				if (groupMapping.getSaleGroupLevel() != null) {
					__groupMapping.setOrderLevelMua(groupMapping.getSaleGroupLevel().getOrder());
					__groupMapping.setMinQuantityMua(groupMapping.getSaleGroupLevel().getMinQuantity());
					__groupMapping.setMinAmountMua(groupMapping.getSaleGroupLevel().getMinAmount());
				}
				if (groupMapping.getPromotionGroupLevel() != null) {
					__groupMapping.setOrderLevelKM(groupMapping.getPromotionGroupLevel().getOrder());
					__groupMapping.setMaxQuantityKM(groupMapping.getPromotionGroupLevel().getMaxQuantity());
					__groupMapping.setPercentKM(groupMapping.getPromotionGroupLevel().getPercent());
					__groupMapping.setIdLevelKM(groupMapping.getPromotionGroupLevel().getId());
				}
				if (groupMapping.getPromotionGroup() != null) {
					__groupMapping.setGroupKMCode(groupMapping.getPromotionGroup().getProductGroupCode());
					__groupMapping.setGroupKMName(groupMapping.getPromotionGroup().getProductGroupName());
					if (groupMapping.getPromotionGroup().getPromotionProgram() != null) {
						promotionProgramMgr.updateMD5ValidCode(groupMapping.getPromotionGroup().getPromotionProgram(), getLogInfoVO());
					}
				}
			}
			result.put(ERROR, false);
			result.put("groupMapping", __groupMapping);
		} catch (Exception e) {
			result.put(ERROR, true);
			result.put("errMsg", ValidateUtil.getErrorMsg(ConstantManager.ERR_SYSTEM));
			LogUtility.logError(e, e.getMessage());
		}
		return SUCCESS;
	}

	/**
	 * load list mapping
	 * 
	 * @author phut
	 * @return
	 */
	public String newListMapLevel() {
		actionStartTime = new Date();
		try {
			ObjectVO<NewLevelMapping> vo = promotionProgramMgr.getListMappingLevel(groupMuaId, groupKMId, fromLevel, toLevel);
			listNewMapping = vo.getLstObject();
			result.put("listNewMapping", listNewMapping);
			result.put("total", vo.getkPaging().getMaxResult());
			if (listNewMapping != null && listNewMapping.isEmpty()) {
				List<NewLevelMapping> __listNewMapping = promotionProgramMgr.getListMappingLevel(groupMuaId, groupKMId, null, null).getLstObject();
				if (__listNewMapping != null && __listNewMapping.isEmpty()) {
					result.put("isNew", true);
				} else {
					result.put("isNew", false);
				}
			} else {
				result.put("isNew", false);
			}
		} catch (Exception e) {
			result.put("isNew", false);
			result.put("listNewMapping", new ArrayList<NewLevelMapping>());
			result.put("total", 0);
			LogUtility.logErrorStandard(e, R.getResource("web.log.message.error", "vnm.web.action.program.PromotionCatalogAction.newListMapLevel"), createLogErrorStandard(actionStartTime));
			result.put("errMsg", ValidateUtil.getErrorMsg(ConstantManager.ERR_SYSTEM));
			result.put(ERROR, true);
		}
		return SUCCESS;
	}

	public String getMaxOrderNumber() {
		actionStartTime = new Date();
		try {
			Integer stt = promotionProgramMgr.getMaxOrderNumberOfGroupLevel(groupMuaId);
			result.put(ERROR, false);
			if (stt == null || stt == 0) {
				stt = 1;
			} else {
				stt = stt + 1;
			}
			result.put("stt", stt);
		} catch (Exception e) {
			LogUtility.logErrorStandard(e, R.getResource("web.log.message.error", "vnm.web.action.program.PromotionCatalogAction.getMaxOrderNumber"), createLogErrorStandard(actionStartTime));
			result.put("errMsg", ValidateUtil.getErrorMsg(ConstantManager.ERR_SYSTEM));
			result.put(ERROR, true);
		}
		return SUCCESS;
	}

	/**
	 * get selected in screen update CTKM
	 * 
	 * @return
	 */
	public String getSelectQuantity() {
		actionStartTime = new Date();
		try {
			Integer quantityUnit = promotionProgramMgr.getSelectQuantity(groupKMId, levelKMId);
			result.put(ERROR, false);
			result.put("quantityUnit", quantityUnit);
		} catch (Exception e) {
			LogUtility.logErrorStandard(e, R.getResource("web.log.message.error", "vnm.web.action.program.PromotionCatalogAction.getMaxOrderNumber"), createLogErrorStandard(actionStartTime));
			result.put("errMsg", ValidateUtil.getErrorMsg(ConstantManager.ERR_SYSTEM));
			result.put(ERROR, true);
		}
		return SUCCESS;
	}

	/**
	 * tao 2 level cho 2 group map cho 2 level
	 * 
	 * @return NewLevelMapping
	 * 
	 * @modify hunglm16
	 * @since 08-09-2015
	 */
	public String newAddLevel() {
		actionStartTime = new Date();
		resetToken(result);
		try {
			GroupLevel existGroupLevel;
			if(!StringUtil.isNullOrEmpty(levelCode))
				levelCode = levelCode.trim();
			if (levelMuaId == null || levelMuaId == 0 || levelKMId == null || levelKMId == 0) {
				existGroupLevel = promotionProgramMgr.getGroupLevelByLevelCode(groupMuaId, null, stt);
				if (existGroupLevel != null) {
					result.put(ERROR, true);
					result.put("errMsg", Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "promotion.program.group.level.code.exist"));
					return SUCCESS;
				}
				NewLevelMapping newMapping = promotionProgramMgr.newAddLevel(groupMuaId, groupKMId, levelCode, stt, quantityUnit, getLogInfoVO());
				result.put(ERROR, false);
				result.put("newMapping", newMapping);
			} else {
				existGroupLevel = promotionProgramMgr.getGroupLevelByLevelCode(groupMuaId, null, stt);
				if (existGroupLevel != null && !existGroupLevel.getId().equals(levelMuaId)) {
					result.put(ERROR, true);
					result.put("errMsg", Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "promotion.program.group.level.code.exist"));
					return SUCCESS;
				}
				NewLevelMapping newMapping = promotionProgramMgr.newUpdateLevel(groupMuaId, groupKMId, levelMuaId, levelKMId, levelCode, stt, quantityUnit, getLogInfoVO());
				if (existGroupLevel != null && existGroupLevel.getProductGroup() != null && existGroupLevel.getProductGroup().getPromotionProgram() != null) {
					promotionProgramMgr.updateMD5ValidCode(existGroupLevel.getProductGroup().getPromotionProgram(), getLogInfoVO());
				}
				result.put(ERROR, false);
				result.put("newMapping", newMapping);
			}
		} catch (Exception e) {
			LogUtility.logErrorStandard(e, R.getResource("web.log.message.error", "vnm.web.action.program.PromotionCatalogAction.newAddLevel"), createLogErrorStandard(actionStartTime));
			result.put("errMsg", ValidateUtil.getErrorMsg(ConstantManager.ERR_SYSTEM));
			result.put(ERROR, true);
		}
		return SUCCESS;
	}

	/**
	 * Cap nhat ham them nhom co cau khuyen mai
	 * 
	 * @modify hunglm16
	 * @return
	 * @since September 08, 2015
	 */
	public String newSaveLevel() {
		actionStartTime = new Date();
		resetToken(result);
		try {
			if (mappingId == null || mappingId == 0) {
				result.put("errMsg", ValidateUtil.getErrorMsg(ConstantManager.ERR_SYSTEM));
				result.put(ERROR, true);
				return SUCCESS;
			}
			/**
			 * check Cac muc trong nhom phai co so luong/so tien khac nhau.
			 * Khong the muc 1 la 1A, 2B muc 2 cung la 1A, 2B
			 */
			//trung nguyen
			List<ExMapping> lstSubCheck = listSubLevelMua;
			Integer checkLevel = 0;
			ProductGroup pg = promotionProgramMgr.getrecursive(groupMuaId);
			int recursive = pg.getRecursive();
			int sizeGroupLevel = promotionProgramMgr.getSize(groupMuaId).size();
			boolean isFlag = false;
			if (sizeGroupLevel > 0){
				for (int i = 0; i< sizeGroupLevel; i++){
					Integer conditionGroupLevel = promotionProgramMgr.getSize(groupMuaId).get(i).getCondition();
					if (conditionGroupLevel != null){
						isFlag = true;
					}
						
				}
			}
			if("ZV21".equals(typeCode) || "ZV20".equals(typeCode)){
				if(recursive==1){
					if(listSubLevelGroupZV192021!=null){
						if (isFlag == false && sizeGroupLevel > 2){
							result.put("errMsg", Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "promotion.program.group.level.code.maping.net.same"));
							result.put(ERROR, true);
							return SUCCESS;
						}
						lstSubCheck = listSubLevelGroupZV192021;
						checkLevel = promotionProgramMgr.checkLevel(mappingId, lstSubCheck, listSubLevelKM);
					}else{
						if (sizeGroupLevel > 1 && isFlag){
						result.put("errMsg", Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "promotion.program.group.level.code.maping.net.same"));
						result.put(ERROR, true);
						return SUCCESS;
						}
					}
				}else{
					if(listSubLevelGroupZV192021!=null){
						checkLevel = 0;
					}
				}
			}else{
				checkLevel = promotionProgramMgr.checkLevel(mappingId, lstSubCheck, listSubLevelKM);
			}
			if (checkLevel == 1) {
				result.put("errMsg", Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "promotion.program.group.level.code.maping.exist"));
				result.put(ERROR, true);
				return SUCCESS;
			} else if (checkLevel == 2) {
				result.put("errMsg", Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "promotion.program.group.level.code.maping.net.same"));
				result.put(ERROR, true);
				return SUCCESS;
			} else if (checkLevel == 3) {
				result.put("errMsg", Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "promotion.program.group.level.code.maping.value.not.same"));
				result.put(ERROR, true);
				return SUCCESS;
			}

			
			
			
			Map<String, Object> returnMap = promotionProgramMgr.newSaveSublevel(mappingId, stt, listSubLevelMua, listSubLevelKM, getLogInfoVO(), listSubLevelGroupZV192021);
			//Map<String, Object> returnMapGroupZv192021 = promotionProg	ramMgr.newSaveGroupZV192021(mappingId, stt, listSubLevelMua, listSubLevelKM, getLogInfoVO(), listSubLevelGroupZV192021);
			
			result.put(ERROR, false);
			if (returnMap.get("listSubLevelMua") != null) {
				result.put("listSubLevelMua", returnMap.get("listSubLevelMua"));
			}
			if (returnMap.get("listSubLevelKM") != null) {
				result.put("listSubLevelKM", returnMap.get("listSubLevelKM"));
			}
			if (mappingId != null) {
				GroupMapping mapping = promotionProgramMgr.getGroupMappingById(mappingId);
				if (mapping != null && mapping.getPromotionGroup() != null && mapping.getPromotionGroup().getPromotionProgram() != null) {
					PromotionProgram program = mapping.getPromotionGroup().getPromotionProgram();
					promotionProgramMgr.updateMD5ValidCode(program, getLogInfoVO());
					if (mapping.getSaleGroup() != null) {
						promotionProgramMgr.updateGroupLevelOrderNumber(program.getId(), mapping.getSaleGroup().getId(), getLogInfoVO());
					}
				}
			}
		} catch (IllegalArgumentException ie) {
			String msg = ie.getMessage();
			if (msg != null) {
				if (msg.contains(PromotionProgramMgr.DUPLICATE_PRODUCT_IN_PRODUCT_GROUPS)) {
					int idx = msg.indexOf(PromotionProgramMgr.DUPLICATE_PRODUCT_IN_PRODUCT_GROUPS);
					if (idx > -1) {
						String err = msg.substring(idx);
						err = err.replace(PromotionProgramMgr.DUPLICATE_PRODUCT_IN_PRODUCT_GROUPS, "");
						result.put(ERROR, true);
						result.put("errMsg", R.getResource("promotion.catalog.import.duplicate.product.in.product.groups", err));
						return SUCCESS;
					}
				} else if (msg.contains(PromotionProgramMgr.DUPLICATE_LEVELS)) {
					result.put(ERROR, true);
					result.put("errMsg", R.getResource("promotion.catalog.import.duplicate.level"));
					return SUCCESS;
				}
			}
			LogUtility.logErrorStandard(ie, R.getResource("web.log.message.error", "vnm.web.action.program.PromotionCatalogAction.newSaveLevel"), createLogErrorStandard(actionStartTime));
			result.put("errMsg", ValidateUtil.getErrorMsg(ConstantManager.ERR_SYSTEM));
			result.put(ERROR, true);
		} catch (Exception e) {
			LogUtility.logErrorStandard(e, R.getResource("web.log.message.error", "vnm.web.action.program.PromotionCatalogAction.newSaveLevel"), createLogErrorStandard(actionStartTime));
			result.put("errMsg", ValidateUtil.getErrorMsg(ConstantManager.ERR_SYSTEM));
			result.put(ERROR, true);
		}
		return SUCCESS;
	}

	public String newCopyLevel() {
		actionStartTime = new Date();
		resetToken(result);
		try {
			listNewMapping = promotionProgramMgr.newCopyLevel(mappingId, copyNum, getLogInfoVO());
			if (mappingId != null) {
				GroupMapping mapping = promotionProgramMgr.getGroupMappingById(mappingId);
				if (mapping != null && mapping.getPromotionGroup() != null && mapping.getPromotionGroup().getPromotionProgram() != null) {
					promotionProgramMgr.updateMD5ValidCode(mapping.getPromotionGroup().getPromotionProgram(), getLogInfoVO());
				}
			}
			result.put("list", listNewMapping);
		} catch (Exception e) {
			LogUtility.logErrorStandard(e, R.getResource("web.log.message.error", "vnm.web.action.program.PromotionCatalogAction.newCopyLevel"), createLogErrorStandard(actionStartTime));
			result.put("errMsg", ValidateUtil.getErrorMsg(ConstantManager.ERR_SYSTEM));
			result.put(ERROR, true);
		}
		return SUCCESS;
	}

	public String newDeleteLevel() {
		actionStartTime = new Date();
		resetToken(result);
		try {
			promotionProgramMgr.newDeleteLevel(mappingId, levelMuaId, levelKMId, getLogInfoVO());
			if (mappingId != null) {
				GroupMapping mapping = promotionProgramMgr.getGroupMappingById(mappingId);
				if (mapping != null && mapping.getPromotionGroup() != null && mapping.getPromotionGroup().getPromotionProgram() != null) {
					promotionProgramMgr.updateMD5ValidCode(mapping.getPromotionGroup().getPromotionProgram(), getLogInfoVO());
				}
			}
			result.put(ERROR, false);
		} catch (Exception e) {
			LogUtility.logErrorStandard(e, R.getResource("web.log.message.error", "vnm.web.action.program.PromotionCatalogAction.newDeleteLevel"), createLogErrorStandard(actionStartTime));
			result.put("errMsg", ValidateUtil.getErrorMsg(ConstantManager.ERR_SYSTEM));
			result.put(ERROR, true);
		}
		return SUCCESS;
	}

	public String newDeleteSubLevel() {
		actionStartTime = new Date();
		resetToken(result);
		try {
			PromotionProgram program = promotionProgramMgr.newDeleteSubLevel(levelId, getLogInfoVO());
			if (program != null) {
				promotionProgramMgr.updateMD5ValidCode(program, getLogInfoVO());
			}
			result.put(ERROR, false);
		} catch (Exception e) {
			LogUtility.logErrorStandard(e, R.getResource("web.log.message.error", "vnm.web.action.program.PromotionCatalogAction.newDeleteSubLevel"), createLogErrorStandard(actionStartTime));
			result.put("errMsg", ValidateUtil.getErrorMsg(ConstantManager.ERR_SYSTEM));
			result.put(ERROR, true);
		}
		return SUCCESS;
	}
	public String newDetailDetailLevel() {
		actionStartTime = new Date();
		resetToken(result);
		try {
			PromotionProgram program = promotionProgramMgr.deleteLevelDetail(levelDetailId, getLogInfoVO());
			if (program != null) {
				promotionProgramMgr.updateMD5ValidCode(program, getLogInfoVO());
			}
		} catch (Exception e) {
			LogUtility.logErrorStandard(e, R.getResource("web.log.message.error", "vnm.web.action.program.PromotionCatalogAction.newDetailDetailLevel"), createLogErrorStandard(actionStartTime));
			result.put("errMsg", ValidateUtil.getErrorMsg(ConstantManager.ERR_SYSTEM));
			result.put(ERROR, true);
		}
		return SUCCESS;
	}

	/**
	 * Xem thong tin don vi CTKM
	 * 
	 * @author lacnv1
	 * @since Aug 14, 2014
	 */
	public String viewDetailShop() throws Exception {
		
		return SUCCESS;
	}

	/**
	 * Tim kiem don vi thuoc CTMK
	 * 
	 * @author lacnv1
	 * @since Aug 14, 2014
	 * 
	 * @author hunglm16
	 * @since March 23,2015
	 */
	public String searchShopOfPromotion() throws Exception {
		actionStartTime = new Date();
		if (promotionId == null || promotionId <= 0) {
			return JSON;
		}
		try {
			Long shId = currentUser.getShopRoot().getShopId();
			PromotionShopMapFilter filter = new PromotionShopMapFilter();
			filter.setShopId(shId);
			filter.setPromotionId(promotionId);
			filter.setStaffRootId(currentUser.getStaffRoot().getStaffId());
			filter.setRoleId(currentUser.getRoleToken().getRoleId());
			filter.setShopRootId(currentUser.getShopRoot().getShopId());
			filter.setShopCode(code);
			filter.setShopName(name);
			filter.setQuantityMax(quantity);
			List<PromotionShopVO> lstTemp = promotionProgramMgr.searchPromotionShopMapJoin(filter);
			List<TreeGridNode<PromotionShopVO>> tree = new ArrayList<TreeGridNode<PromotionShopVO>>();
			if (lstTemp == null || lstTemp.size() == 0) {
				result.put("rows", tree);
				return JSON;
			}
			for (int k = 0; k < lstTemp.size(); k++) {
				/*
				 * for (int j = k + 1; j < lstTemp.size(); j++) { if
				 * (lstTemp.get(k).getId().equals(lstTemp.get(j).getId())) {
				 * lstTemp.remove(j); j--; } }
				 */
				//fix sonar
				int j = k + 1;
				while (j < lstTemp.size()) {
					if (lstTemp.get(k).getId().equals(lstTemp.get(j).getId())) {
						lstTemp.remove(j);
					} else {
						j++;
					}
				}
			}
			//Filter cac shop trung
			ArrayList<PromotionShopVO> lst = new ArrayList<>();
			for (PromotionShopVO shopVO : lstTemp) {
				boolean isExist = false;

				for (PromotionShopVO shopAdd : lst) {
					if (shopVO.getId().equals(shopAdd.getId())) {
						isExist = true;
					}
				}

				if (!isExist) {
					lst.add(shopVO);
				}
			}
			// Tao cay
			int i, sz = lst.size();
			PromotionShopVO vo = null;
			boolean flag = false;
			for (i = 0; i < sz; i++) {
				vo = lst.get(i);
				if (shId.equals(vo.getId())) {
					i++;
					flag = true;
					break;
				}
			}
			if (!flag) {
				result.put("rows", tree);
				return JSON;
			}
			//PromotionShopVO vo = lst.get(0);
			TreeGridNode<PromotionShopVO> node = new TreeGridNode<PromotionShopVO>();
			node.setNodeId(vo.getId().toString());
			node.setAttr(vo);
			node.setState(ConstantManager.JSTREE_STATE_OPEN);
			node.setText(vo.getShopCode() + " - " + vo.getShopName());
			List<TreeGridNode<PromotionShopVO>> chidren = new ArrayList<TreeGridNode<PromotionShopVO>>();
			node.setChildren(chidren);
			tree.add(node);

			TreeGridNode<PromotionShopVO> tmp;
			TreeGridNode<PromotionShopVO> tmp2;
			for (; i < sz; i++) {
				vo = lst.get(i);

				if (vo.getParentId() == null) {
					continue;
				}

				tmp2 = getNodeFromTree(tree, vo.getParentId().toString());
				if (tmp2 != null) {
					tmp = new TreeGridNode<PromotionShopVO>();
					tmp.setNodeId(vo.getId().toString());
					tmp.setAttr(vo);
					if (0 == vo.getIsNPP()) {
						tmp.setState(ConstantManager.JSTREE_STATE_OPEN);
					} else {
						tmp.setState(ConstantManager.JSTREE_STATE_LEAF);
					}
					tmp.setText(vo.getShopCode() + " - " + vo.getShopName());

					if (tmp2.getChildren() == null) {
						tmp2.setChildren(new ArrayList<TreeGridNode<PromotionShopVO>>());
					}
					tmp2.getChildren().add(tmp);
				}
			}

			result.put("rows", tree);
		} catch (Exception ex) {
			LogUtility.logErrorStandard(ex, R.getResource("web.log.message.error", "vnm.web.action.program.PromotionCatalogAction.searchShopOfPromotion"), createLogErrorStandard(actionStartTime));
			result.put("errMsg", ValidateUtil.getErrorMsg(ConstantManager.ERR_SYSTEM));
			result.put(ERROR, true);
		}
		return JSON;
	}

	/**
	 * Lay node trong cay
	 * 
	 * @author lacnv1
	 * @since Aug 15, 2014
	 */
	private <T> TreeGridNode<T> getNodeFromTree(List<TreeGridNode<T>> treeTmp, String nodeId) throws Exception {
		if (treeTmp == null) {
			return null;
		}
		TreeGridNode<T> node;
		TreeGridNode<T> tmp;
		for (int i = 0, sz = treeTmp.size(); i < sz; i++) {
			node = treeTmp.get(i);
			if (node.getNodeId().equals(nodeId)) {
				return node;
			}
			tmp = getNodeFromTree(node.getChildren(), nodeId);
			if (tmp != null) {
				return tmp;
			}
		}
		return null;
	}

	/**
	 * Xem thong thuoc tinh KH CTKM
	 * 
	 * @author lacnv1
	 * @since Aug 16, 2014
	 */
	public String viewCustomerAttribute() throws Exception {
		actionStartTime = new Date();
		if (promotionId != null && promotionId != 0) {
			try {
				lstPromotionCustAttrVO = promotionProgramMgr.getListPromotionCustAttrVOCanBeSet(null, promotionId);
				//listPromotionCustAttrVOAlreadySet = promotionProgramMgr.getListPromotionCustAttrVOAlreadySet(null, promotionId);
			} catch (Exception ex) {
				LogUtility.logErrorStandard(ex, R.getResource("web.log.message.error", "vnm.web.action.program.PromotionCatalogAction.viewCustomerAttribute"), createLogErrorStandard(actionStartTime));
				errMsg = ValidateUtil.getErrorMsg(ConstantManager.ERR_SYSTEM);
				result.put("errMsg", errMsg);
				isError = true;
				result.put(ERROR, isError);
			}
		}
		return SUCCESS;
	}

	/**
	 * Tim kiem khach hang thuoc CTMK
	 * 
	 * @author lacnv1
	 * @since Aug 15, 2014
	 */
	public String searchCustomerOfPromotion() throws Exception {
		actionStartTime = new Date();
		if (promotionId == null || promotionId <= 0) {
			result.put("rows", new ArrayList<PromotionCustomerVO>());
			result.put("total", 0);
			return JSON;
		}
		try {
			PromotionCustomerFilter filter = new PromotionCustomerFilter();
			filter.setPromotionId(promotionId);
			filter.setCode(code);
			filter.setName(name);
			filter.setAddress(address);
			filter.setIsCustomerOnly(false);
			if (shopId == null || shopId == 0) {
				filter.setStrListShopId(getStrListShopId());
			} else {
				filter.setShopId(shopId);
			}
			ObjectVO<PromotionCustomerVO> obj = promotionProgramMgr.getCustomerInPromotionProgram(filter);
			List<PromotionCustomerVO> lst = obj.getLstObject();

			List<TreeGridNode<PromotionCustomerVO>> tree = new ArrayList<TreeGridNode<PromotionCustomerVO>>();
			if (lst == null || lst.size() == 0) {
				result.put("rows", tree);
				return JSON;
			}

			// Tao cay
			int i, sz = lst.size();
			PromotionCustomerVO vo = null;
			Long shId = currentUser.getShopRoot().getShopId();
			for (i = 0; i < sz; i++) {
				vo = lst.get(i);
				if (vo.getIsCustomer() == 0 && shId.equals(vo.getId())) {
					i++;
					break;
				}
			}
			//PromotionStaffVO vo = lst.get(0);
			TreeGridNode<PromotionCustomerVO> node = new TreeGridNode<PromotionCustomerVO>();
			node.setNodeId("sh" + vo.getId());
			node.setAttr(vo);
			node.setState(ConstantManager.JSTREE_STATE_OPEN);
			node.setText(vo.getCustomerCode() + " - " + vo.getCustomerName());
			List<TreeGridNode<PromotionCustomerVO>> chidren = new ArrayList<TreeGridNode<PromotionCustomerVO>>();
			node.setChildren(chidren);
			tree.add(node);

			TreeGridNode<PromotionCustomerVO> tmp;
			TreeGridNode<PromotionCustomerVO> tmp2;
			for (; i < sz; i++) {
				vo = lst.get(i);

				tmp2 = getNodeFromTree(tree, "sh" + vo.getParentId());
				if (tmp2 != null) {
					tmp = new TreeGridNode<PromotionCustomerVO>();
					tmp.setAttr(vo);
					if (0 == vo.getIsCustomer()) {
						tmp.setNodeId("sh" + vo.getId());
						tmp.setState(ConstantManager.JSTREE_STATE_OPEN);
					} else {
						tmp.setNodeId("st" + vo.getId());
						tmp.setState(ConstantManager.JSTREE_STATE_LEAF);
					}
					tmp.setText(vo.getCustomerCode() + " - " + vo.getCustomerName());

					if (tmp2.getChildren() == null) {
						tmp2.setChildren(new ArrayList<TreeGridNode<PromotionCustomerVO>>());
					}
					tmp2.getChildren().add(tmp);
				}
			}

			result.put("rows", tree);
		} catch (Exception ex) {
			LogUtility.logErrorStandard(ex, R.getResource("web.log.message.error", "vnm.web.action.program.PromotionCatalogAction.searchCustomerOfPromotion"), createLogErrorStandard(actionStartTime));
			result.put("errMsg", ValidateUtil.getErrorMsg(ConstantManager.ERR_SYSTEM));
			result.put(ERROR, true);
		}
		return JSON;
	}

	/**
	 * Tim kiem don vi thuoc CTMK
	 * 
	 * @author lacnv1
	 * @since Aug 14, 2014
	 */
	public String searchSalerOfPromotion() throws Exception {
		actionStartTime = new Date();
		if (promotionId == null || promotionId <= 0) {
			return JSON;
		}
		try {
			List<PromotionStaffVO> lst = promotionProgramMgr.getSalerInPromotionProgram(promotionId, code, name, quantity);

			List<TreeGridNode<PromotionStaffVO>> tree = new ArrayList<TreeGridNode<PromotionStaffVO>>();
			if (lst == null || lst.size() == 0) {
				result.put("rows", tree);
				return JSON;
			}

			// Tao cay
			int i, sz = lst.size();
			PromotionStaffVO vo = null;
			Long shId = currentUser.getShopRoot().getShopId();
			for (i = 0; i < sz; i++) {
				vo = lst.get(i);
				if (vo.getIsSaler() == 0 && shId.equals(vo.getId())) {
					i++;
					break;
				}
			}
			//PromotionStaffVO vo = lst.get(0);
			TreeGridNode<PromotionStaffVO> node = new TreeGridNode<PromotionStaffVO>();
			node.setNodeId("sh" + vo.getId());
			node.setAttr(vo);
			node.setState(ConstantManager.JSTREE_STATE_OPEN);
			node.setText(vo.getCode() + " - " + vo.getName());
			List<TreeGridNode<PromotionStaffVO>> chidren = new ArrayList<TreeGridNode<PromotionStaffVO>>();
			node.setChildren(chidren);
			tree.add(node);

			TreeGridNode<PromotionStaffVO> tmp;
			TreeGridNode<PromotionStaffVO> tmp2;
			for (; i < sz; i++) {
				vo = lst.get(i);

				tmp2 = getNodeFromTree(tree, "sh" + vo.getParentId());
				if (tmp2 != null) {
					tmp = new TreeGridNode<PromotionStaffVO>();
					tmp.setAttr(vo);
					if (0 == vo.getIsSaler()) {
						tmp.setNodeId("sh" + vo.getId());
						tmp.setState(ConstantManager.JSTREE_STATE_OPEN);
					} else {
						tmp.setNodeId("st" + vo.getId());
						tmp.setState(ConstantManager.JSTREE_STATE_LEAF);
					}
					tmp.setText(vo.getCode() + " - " + vo.getName());

					if (tmp2.getChildren() == null) {
						tmp2.setChildren(new ArrayList<TreeGridNode<PromotionStaffVO>>());
					}
					tmp2.getChildren().add(tmp);
				}
			}

			result.put("rows", tree);
		} catch (Exception ex) {
			LogUtility.logErrorStandard(ex, R.getResource("web.log.message.error", "vnm.web.action.program.PromotionCatalogAction.searchSalerOfPromotion"), createLogErrorStandard(actionStartTime));
			result.put("errMsg", ValidateUtil.getErrorMsg(ConstantManager.ERR_SYSTEM));
			result.put(ERROR, true);
		}
		return JSON;
	}

	/**
	 * Tim kiem KH them vao CTKM
	 * 
	 * @author lacnv1
	 * @since Aug 18, 2014
	 */
//	public String searchCustomerOnDlg() throws Exception {
//		actionStartTime = new Date();
//		if (promotionId == null || promotionId <= 0) {
//			result.put("rows", new ArrayList<PromotionCustomerVO>());
//			result.put("total", 0);
//			return JSON;
//		}
//		try {
//			KPaging<PromotionCustomerVO> paging = new KPaging<PromotionCustomerVO>();
//			paging.setPage(page - 1);
//			paging.setPageSize(max);
//			PromotionCustomerFilter filter = new PromotionCustomerFilter();
//			filter.setkPaging(paging);
//			filter.setPromotionId(promotionId);
//			filter.setCode(code);
//			filter.setName(name);
//			filter.setAddress(address);
//			if (shopId == null || shopId == 0) {
//				filter.setStrListShopId(getStrListShopId());
//			} else {
//				filter.setShopId(shopId);
//			}
//			ObjectVO<PromotionCustomerVO> obj = promotionProgramMgr.searchCustomerForPromotionProgram(filter);
//			;
//			if (obj == null) {
//				result.put("rows", new ArrayList<PromotionCustomerVO>());
//				result.put("total", 0);
//				return JSON;
//			}
//
//			result.put("rows", obj.getLstObject());
//			result.put("total", obj.getkPaging().getTotalRows());
//		} catch (Exception ex) {
//			LogUtility.logErrorStandard(ex, R.getResource("web.log.message.error", "vnm.web.action.program.PromotionCatalogAction.searchCustomerOnDlg"), createLogErrorStandard(actionStartTime));
//			result.put("errMsg", ValidateUtil.getErrorMsg(ConstantManager.ERR_SYSTEM));
//			result.put(ERROR, true);
//		}
//		return JSON;
//	}
	public String getStrListShopId(List<Long> list) {
		List<Long> lstShop = list;
		String str = "-1";
		for (Long id : lstShop) {
			str += "," + id.toString();
		}
		return str;
	}
	
	public String searchCustomerOnDlg() throws Exception {
		actionStartTime = new Date();
		if (promotionId == null || promotionId <= 0) {
			result.put("rows", new ArrayList<PromotionCustomerVO>());
			result.put("total", 0);
			return JSON;
		}
		try {
			
			// ptquang
			Long shId = currentUser.getShopRoot().getShopId();
			PromotionShopMapFilter fil = new PromotionShopMapFilter();
			fil.setShopId(shId);
			fil.setPromotionId(promotionId);
			fil.setStaffRootId(currentUser.getStaffRoot().getStaffId());
			fil.setRoleId(currentUser.getRoleToken().getRoleId());
			fil.setShopRootId(currentUser.getShopRoot().getShopId());
			fil.setShopCode(code);
			fil.setShopName(name);
			fil.setQuantityMax(quantity);
			List<Long> splitShop_Id = new ArrayList<Long>();
			List<PromotionShopVO> lstTemp = promotionProgramMgr.searchPromotionShopMapJoin(fil);
			if (lstTemp.size() > 0){
				for (int i = 0; i < lstTemp.size(); i++) {
					if(lstTemp.get(i).getIsNPP() == 1 || lstTemp.get(i).getIsNPP().equals(1)) {
						splitShop_Id.add(lstTemp.get(i).getId());
					}
				}
				
				// end ptquang
				KPaging<PromotionCustomerVO> paging = new KPaging<PromotionCustomerVO>();
				paging.setPage(page - 1);
				paging.setPageSize(max);
				PromotionCustomerFilter filter = new PromotionCustomerFilter();
				filter.setkPaging(paging);
				filter.setPromotionId(promotionId);
				filter.setCode(code);
				filter.setName(name);
				filter.setAddress(address);
				if (shopId == null || shopId == 0) {
					//filter.setStrListShopId(getStrListShopId());
					String slpShop_Id = getStrListShopId(splitShop_Id);
					filter.setStrListShopId(slpShop_Id);
				} else {
					filter.setShopId(shopId);
				}
				ObjectVO<PromotionCustomerVO> obj = promotionProgramMgr.searchCustomerForPromotionProgram(filter);
				if (obj == null) {
					result.put("rows", new ArrayList<PromotionCustomerVO>());
					result.put("total", 0);
					return JSON;
				}
				result.put("rows", obj.getLstObject());
				result.put("total", obj.getkPaging().getTotalRows());
			} else {
				result.put("rows", new ArrayList<PromotionCustomerVO>());
				result.put("total", 0);
				return JSON;
			}
		} catch (Exception ex) {
			LogUtility.logErrorStandard(ex, R.getResource("web.log.message.error", "vnm.web.action.program.PromotionCatalogAction.searchCustomerOnDlg"), createLogErrorStandard(actionStartTime));
			result.put("errMsg", ValidateUtil.getErrorMsg(ConstantManager.ERR_SYSTEM));
			result.put(ERROR, true);
		}
		return JSON;
	}

	/**
	 * Them KH vao CTKM
	 * 
	 * @author lacnv1
	 * @since Aug 19, 2014
	 */
	public String addPromotionCustomer() throws Exception {
		actionStartTime = new Date();
		resetToken(result);
		result.put(ERROR, true);
		if (promotionId == null || promotionId < 1 || lstId == null || lstId.size() == 0) {
			result.put("errMsg", ValidateUtil.getErrorMsg(ConstantManager.ERR_NOT_DATA_CORRECT));
			return JSON;
		}
		try {
			staff = getStaffByCurrentUser();
			if (staff == null || staff.getShop() == null || staff.getStaffType() == null) {
				result.put("errMsg", ValidateUtil.getErrorMsg(ConstantManager.ERR_NOT_PERMISSION, ""));
				return JSON;
			}

			if (shopId == null || shopId <= 0) {
				shopId = getCurrentShop().getId();
			}

			List<PromotionCustomerVO> lstTmp = promotionProgramMgr.getListCustomerInPromotion(promotionId, lstId);
			if (lstTmp != null && lstTmp.size() > 0) {
				String msg = "";
				for (PromotionCustomerVO vo : lstTmp) {
					msg += (", " + vo.getCustomerCode());
				}
				msg = msg.replaceFirst(", ", "");
				result.put("errMsg", Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "catalog.promotion.customer.map.exists", msg));
				return JSON;
			}
			//			lstTmp = null;
			PromotionProgram pro = promotionProgramMgr.getPromotionProgramById(promotionId);
			if (pro == null) {
				result.put("errMsg", ValidateUtil.getErrorMsg(ConstantManager.ERR_NOT_EXIST_DB, "CTKM"));
				return JSON;
			}
			if (!ActiveType.WAITING.equals(pro.getStatus()) && !ActiveType.RUNNING.equals(pro.getStatus())) {
				result.put("errMsg", Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "promotion.program.incorrect"));
				return JSON;
			}
			List<PromotionShopMap> promotionShopMaps = promotionProgramMgr.getPromotionShopMapByCustomer(lstId, promotionId);

			List<PromotionCustomerMap> lst = new ArrayList<PromotionCustomerMap>();

			PromotionCustomerMap pcm = null;
			Customer cust;
			Date now = DateUtil.now();
			for (Long idt : lstId) {
				cust = customerMgr.getCustomerById(idt);
				if (cust == null) {
					result.put("errMsg", ValidateUtil.getErrorMsg(ConstantManager.ERR_NOT_EXIST_DB, "KH"));
					return JSON;
				}
				if (!ActiveType.RUNNING.equals(cust.getStatus())) {
					result.put("errMsg", ValidateUtil.getErrorMsg(ConstantManager.ERR_STATUS_INACTIVE, cust.getShortCode()));
					return JSON;
				}
				for (PromotionShopMap psm : promotionShopMaps) {
					if (psm.getShop().getId().equals(cust.getShop().getId())) {
						List<PromotionCustomerMap> list = promotionProgramMgr.getListPromotionCustomerMapEntity(cust.getId(), psm);
						if (list != null && list.size() > 0) {
							for (PromotionCustomerMap customerMap : list) {
								String customerCode = customerMap.getCustomer().getCustomerCode();
								StringBuilder sb = new StringBuilder("Khách hàng có mã ");
								sb.append(customerCode);
								sb.append(" có ");
								if (quantity != null && customerMap.getQuantityReceivedTotal() != null && quantity.compareTo(customerMap.getQuantityReceivedTotal()) < 0) {
									sb.append("số suất phân bổ không được bé hơn số suất đã nhận.");
									result.put("errMsg", sb.toString());
									return JSON;
								} else if (amount != null && customerMap.getAmountReceivedTotal() != null && amount.compareTo(customerMap.getAmountReceivedTotal()) < 0) {
									sb.append("số tiền phân bổ không được bé hơn số tiền đã nhận.");
									result.put("errMsg", sb.toString());
									return JSON;
								} else if (number != null && customerMap.getNumReceivedTotal() != null && number.compareTo(customerMap.getNumReceivedTotal()) < 0) {
									sb.append("số lượng phân bổ không được bé hơn số lượng đã nhận.");
									result.put("errMsg", sb.toString());
									return JSON;
								}
								if (pcm != null) {
									pcm.setQuantityMax(quantity);
									pcm.setAmountMax(amount);
									pcm.setNumMax(number);
								}
								lst.add(customerMap);
							}
						} else {
							pcm = new PromotionCustomerMap();
							pcm.setPromotionShopMap(psm);
							pcm.setShop(psm.getShop());
							pcm.setCustomer(cust);
							pcm.setQuantityMax(quantity);
							pcm.setAmountMax(amount);
							pcm.setNumMax(number);
							pcm.setStatus(ActiveType.RUNNING);
							pcm.setCreateDate(now);
							pcm.setCreateUser(staff.getStaffCode());
							lst.add(pcm);
						}
					}
				}
			}
			promotionProgramMgr.createListPromotionCustomerMapEx(promotionShopMaps, lst, getLogInfoVO());
			result.put(ERROR, false);
		} catch (Exception ex) {
			LogUtility.logErrorStandard(ex, R.getResource("web.log.message.error", "vnm.web.action.program.PromotionCatalogAction.addPromotionCustomer"), createLogErrorStandard(actionStartTime));
			Throwable e = (Throwable) ex.getCause();
			String errCode = e.getMessage();
			if (errCode.contains("ORA-20024")) {//
				result.put("errMsg", Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "promotion.program.not.meet.conditions.new.cus"));
			} else {
				result.put("errMsg", ValidateUtil.getErrorMsg(ConstantManager.ERR_SYSTEM));
			}
			result.put(ERROR, true);
		}
		return JSON;
	}

	/**
	 * Cap nhat so suat KH
	 * 
	 * @author lacnv1
	 * @since Aug 19, 2014
	 */
	public String updateCustomerQuantity() throws Exception {
		actionStartTime = new Date();
		resetToken(result);
		result.put(ERROR, true);
		if (promotionId == null || promotionId < 1 || id == null || id < 1) {
			result.put("errMsg", ValidateUtil.getErrorMsg(ConstantManager.ERR_NOT_DATA_CORRECT));
			return JSON;
		}
		try {
			staff = getStaffByCurrentUser();
			if (staff == null || staff.getShop() == null || staff.getStaffType() == null) {
				result.put("errMsg", ValidateUtil.getErrorMsg(ConstantManager.ERR_NOT_PERMISSION, ""));
				return JSON;
			}

			PromotionProgram pro = promotionProgramMgr.getPromotionProgramById(promotionId);
			if (pro == null) {
				result.put("errMsg", ValidateUtil.getErrorMsg(ConstantManager.ERR_NOT_EXIST_DB, "CTKM"));
				return JSON;
			}
			if (!ActiveType.WAITING.equals(pro.getStatus()) && !ActiveType.RUNNING.equals(pro.getStatus())) {
				result.put("errMsg", Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "promotion.program.incorrect"));
				return JSON;
			}

			PromotionCustomerMap pcm = promotionProgramMgr.getPromotionCustomerMapById(id);
			if (pcm == null || !ActiveType.RUNNING.equals(pcm.getStatus())) {
				result.put("errMsg", Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "catalog.promotion.customer.map.not.exists"));
				return JSON;
			}
			if (quantity != null && pcm.getQuantityReceivedTotal() != null && quantity.compareTo(pcm.getQuantityReceivedTotal()) < 0) {
				result.put("errMsg", "Số suất phân bổ không được bé hơn số suất đã nhận.");
				return JSON;
			} else if (amount != null && pcm.getAmountReceivedTotal() != null && amount.compareTo(pcm.getAmountReceivedTotal()) < 0) {
				result.put("errMsg", "Số tiền phân bổ không được bé hơn số tiền đã nhận.");
				return JSON;
			} else if (number != null && pcm.getNumReceivedTotal() != null && number.compareTo(pcm.getNumReceivedTotal()) < 0) {
				result.put("errMsg", "Số lượng phân bổ không được bé hơn số lượng đã nhận.");
				return JSON;
			} else {
				pcm.setQuantityMax(quantity);
				pcm.setAmountMax(amount);
				pcm.setNumMax(number);
				promotionProgramMgr.updatePromotionCustomerMap(pcm, getLogInfoVO());
				result.put(ERROR, false);
			}
		} catch (Exception ex) {
			LogUtility.logErrorStandard(ex, R.getResource("web.log.message.error", "vnm.web.action.program.PromotionCatalogAction.updateCustomerQuantity"), createLogErrorStandard(actionStartTime));
			result.put("errMsg", ValidateUtil.getErrorMsg(ConstantManager.ERR_SYSTEM));
			result.put(ERROR, true);
		}
		return JSON;
	}

	/**
	 * Xoa KH
	 * 
	 * @author lacnv1
	 * @since Aug 19, 2014
	 */
	public String deleteCustomer() throws Exception {
		actionStartTime = new Date();
		resetToken(result);
		result.put(ERROR, true);
		if (promotionId == null || promotionId < 1 || id == null || id < 1) {
			result.put("errMsg", ValidateUtil.getErrorMsg(ConstantManager.ERR_NOT_DATA_CORRECT));
			return JSON;
		}
		try {
			staff = getStaffByCurrentUser();
			if (staff == null || staff.getShop() == null || staff.getStaffType() == null) {
				result.put("errMsg", ValidateUtil.getErrorMsg(ConstantManager.ERR_NOT_PERMISSION, ""));
				return JSON;
			}

			PromotionProgram pro = promotionProgramMgr.getPromotionProgramById(promotionId);
			if (pro == null) {
				result.put("errMsg", ValidateUtil.getErrorMsg(ConstantManager.ERR_NOT_EXIST_DB, "CTKM"));
				return JSON;
			}
			if (pro.getStatus().equals(ActiveType.RUNNING)) {
				result.put("errMsg", "Chương trình đang ở trạng thái hoạt động.");
				return JSON;
			}
			PromotionCustomerMap pcm = promotionProgramMgr.getPromotionCustomerMapById(id);
			if (pcm == null || !ActiveType.RUNNING.equals(pcm.getStatus())) {
				result.put("errMsg", Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "catalog.promotion.customer.map.not.exists"));
				return JSON;
			}
			pcm.setStatus(ActiveType.DELETED);
			promotionProgramMgr.updatePromotionCustomerMap(pcm, getLogInfoVO());
			result.put(ERROR, false);
		} catch (Exception ex) {
			LogUtility.logErrorStandard(ex, R.getResource("web.log.message.error", "vnm.web.action.program.PromotionCatalogAction.deleteCustomer"), createLogErrorStandard(actionStartTime));
			result.put("errMsg", ValidateUtil.getErrorMsg(ConstantManager.ERR_SYSTEM));
			result.put(ERROR, true);
		}
		return JSON;
	}

	/**
	 * Cap nhat so suat don vi
	 * 
	 * @author lacnv1
	 * @since Aug 19, 2014
	 */
	public String updateShopQuantity() throws Exception {
		List<PromotionShopMap> lstCheckPromotionShopMap = new ArrayList<PromotionShopMap>();
		List<PromotionShopJoin> lstCheckPromotionShopJoin = new ArrayList<PromotionShopJoin>();
		actionStartTime = new Date();
		resetToken(result);
		result.put(ERROR, true);
		if (promotionId == null || promotionId < 1 || lstShopQttAdd == null || lstShopQttAdd.size() == 0) {
			result.put(ERR_MSG, ValidateUtil.getErrorMsg(ConstantManager.ERR_NOT_DATA_CORRECT));
			return JSON;
		}
		try {
			staff = getStaffByCurrentUser();
			if (staff == null || staff.getShop() == null || staff.getStaffType() == null) {
				result.put("errMsg", ValidateUtil.getErrorMsg(ConstantManager.ERR_NOT_PERMISSION, ""));
				return JSON;
			}
			PromotionProgram pro = promotionProgramMgr.getPromotionProgramById(promotionId);
			if (pro == null) {
				result.put("errMsg", ValidateUtil.getErrorMsg(ConstantManager.ERR_NOT_EXIST_DB, "CTKM"));
				return JSON;
			}
			if (!ActiveType.WAITING.equals(pro.getStatus()) && !ActiveType.RUNNING.equals(pro.getStatus())) {
				result.put("errMsg", Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "promotion.program.incorrect"));
				return JSON;
			}
			List<PromotionShopMap> lstNewPromotionShopMap = new ArrayList<PromotionShopMap>();
			List<PromotionShopJoin> lstNewPromotionShopJoin = new ArrayList<PromotionShopJoin>();
			lstCheckPromotionShopMap = promotionProgramMgr.getListPromotionChildShopMapWithShopAndPromotionProgram(staff.getShop().getId(), promotionId);
			lstCheckPromotionShopJoin = promotionProgramMgr.getListPromotionChildShopJoinWithShopAndPromotionProgram(staff.getShop().getId(), promotionId);
			for (int i = 0, sz = lstShopQttAdd.size(); i < sz; i++) {
				PromotionShopQttVO promotionShopQttVO = lstShopQttAdd.get(i);
				if (promotionShopQttVO != null) {
					PromotionShopMap psm = null;
					PromotionShopJoin psj = null;
					shopId = promotionShopQttVO.getShopId();
					if (super.getMapShopChild().get(shopId) == null) {
						result.put(ERR_MSG, R.getResource("common.cms.shop.undefined"));
						return JSON;
					}
					Shop shop = shopMgr.getShopById(shopId);
					if (shop == null) {
						result.put("errMsg", ValidateUtil.getErrorMsg(ConstantManager.ERR_NOT_EXIST_DB, Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "common.shop.name.lable")));
						return JSON;
					}
					if (!ActiveType.RUNNING.equals(shop.getStatus())) {
						result.put("errMsg", ValidateUtil.getErrorMsg(ConstantManager.ERR_STATUS_INACTIVE, shop.getShopCode()));
						return JSON;
					}
					if (shop.getType() != null && shop.getType().getSpecificType() != null && (ShopSpecificType.NPP.getValue().equals(shop.getType().getSpecificType().getValue()) || ShopSpecificType.NPP_MT.getValue().equals(shop.getType()
							.getSpecificType().getValue()))) {
						for (int j = 0, size = lstCheckPromotionShopMap.size(); j < size; j++) {
							PromotionShopMap item = lstCheckPromotionShopMap.get(j);
							if (item.getShop() != null && item.getShop().getId().equals(shopId)) {
								psm = item;
								break;
							}
						}
						if (psm == null) {
							result.put("errMsg", Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "promotion.shop.not.exist"));
							return JSON;
						} else {
							if (promotionShopQttVO.getQuantityMax() != null && psm.getQuantityReceivedTotal() != null && promotionShopQttVO.getQuantityMax().compareTo(psm.getQuantityReceivedTotal()) < 0) {
								result.put("errMsg", Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "promotion.program.shop.quantity.max.not.less.than.quantity.received"));
								return JSON;
							} else if (promotionShopQttVO.getAmountMax() != null && psm.getAmountReceivedTotal() != null && promotionShopQttVO.getAmountMax().compareTo(psm.getAmountReceivedTotal()) < 0) {
								result.put("errMsg", Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "promotion.program.shop.amount.max.not.less.than.amount.received"));
								return JSON;
							} else if (promotionShopQttVO.getNumMax() != null && psm.getNumReceivedTotal() != null && promotionShopQttVO.getNumMax().compareTo(psm.getNumReceivedTotal()) < 0) {
								result.put("errMsg", Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "promotion.program.shop.num.max.not.less.than.num.received"));
								return JSON;
							}
						}
						psm.setQuantityMax(promotionShopQttVO.getQuantityMax());
						psm.setIsQuantityMaxEdit(promotionShopQttVO.getIsEdit());
						psm.setAmountMax(promotionShopQttVO.getAmountMax());
						psm.setNumMax(promotionShopQttVO.getNumMax());
						lstNewPromotionShopMap.add(psm);
					} else {
						for (int j = 0, size = lstCheckPromotionShopJoin.size(); j < size; j++) {
							PromotionShopJoin item = lstCheckPromotionShopJoin.get(j);
							if (item.getShop() != null && item.getShop().getId().equals(shopId)) {
								psj = item;
								break;
							}
						}
						if (psj == null) {
							result.put("errMsg", Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "promotion.shop.not.exist"));
							return JSON;
						} else {
							if (promotionShopQttVO.getQuantityMax() != null && psj.getQuantityReceivedTotal() != null && promotionShopQttVO.getQuantityMax().compareTo(psj.getQuantityReceivedTotal()) < 0) {
								result.put("errMsg", Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "promotion.program.shop.quantity.max.not.less.than.quantity.received"));
								return JSON;
							} else if (promotionShopQttVO.getAmountMax() != null && psj.getAmountReceivedTotal() != null && promotionShopQttVO.getAmountMax().compareTo(psj.getAmountReceivedTotal()) < 0) {
								result.put("errMsg", Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "promotion.program.shop.amount.max.not.less.than.amount.received"));
								return JSON;
							} else if (promotionShopQttVO.getNumMax() != null && psj.getNumReceivedTotal() != null && promotionShopQttVO.getNumMax().compareTo(psj.getNumReceivedTotal()) < 0) {
								result.put("errMsg", Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "promotion.program.shop.num.max.not.less.than.num.received"));
								return JSON;
							}
						}
						psj.setQuantityMax(promotionShopQttVO.getQuantityMax());
						psj.setIsQuantityMaxEdit(promotionShopQttVO.getIsEdit());
						psj.setAmountMax(promotionShopQttVO.getAmountMax());
						psj.setNumMax(promotionShopQttVO.getNumMax());
						lstNewPromotionShopJoin.add(psj);
					}
				}
			}
			if (ActiveType.RUNNING.equals(pro.getStatus())) {
				// lay cau hinh bat buoc cac node NPP phai duoc phan bo
				List<ApParam> allocationPromotionShopConfigs = apParamMgr.getListApParam(ApParamType.ALLOCATION_PROMOTION_SHOP, ActiveType.RUNNING);
				isAllocationPromotionShop = (allocationPromotionShopConfigs == null || allocationPromotionShopConfigs.size() == 0 || Constant.ONE_TEXT.equals(allocationPromotionShopConfigs.get(0).getValue()));
				if (isAllocationPromotionShop) {
					errMsg = validateQuantityAmountNumPromotionShop(lstCheckPromotionShopMap);
					if (!StringUtil.isNullOrEmpty(errMsg)) {
						result.put("errMsg", errMsg);
						return JSON;
					}
				}
				// 1. kiem tra cha phan bo con phai phan bo: chi kiem tra khi chuyen trang thai sang hoat dong. khi luu chi kiem tra tong con <= cha. 
				errMsg = validateAllocateParentChildPromotionShop(lstCheckPromotionShopMap, lstCheckPromotionShopJoin);
				if (!StringUtil.isNullOrEmpty(errMsg)) {
					result.put("errMsg", errMsg);
					return JSON;
				}
			}
			errMsg = validatePromotionShop(lstCheckPromotionShopMap, lstCheckPromotionShopJoin);
			if (!StringUtil.isNullOrEmpty(errMsg)) {
				result.put("errMsg", errMsg);
				return JSON;
			}
			promotionProgramMgr.updatePromotionShop(lstNewPromotionShopMap, lstNewPromotionShopJoin, getLogInfoVO());
			result.put(ERROR, false);
		} catch (Exception ex) {
			LogUtility.logErrorStandard(ex, R.getResource("web.log.message.error", "vnm.web.action.program.PromotionCatalogAction.updateShopQuantity"), createLogErrorStandard(actionStartTime));
			result.put("errMsg", ValidateUtil.getErrorMsg(ConstantManager.ERR_SYSTEM));
			result.put(ERROR, true);
			return JSON;
		} finally {
			lstCheckPromotionShopMap.clear();
			lstCheckPromotionShopMap = null;
			lstCheckPromotionShopJoin.clear();
			lstCheckPromotionShopJoin = null;
		}
		return JSON;
	}

	/**
	 * Xoa don vi
	 * 
	 * @author lacnv1
	 * @since Aug 19, 2014
	 */
	public String deleteShop() throws Exception {
		actionStartTime = new Date();
		resetToken(result);
		result.put(ERROR, true);
		if (promotionId == null || promotionId < 1 || shopId == null || shopId < 1) {
			result.put("errMsg", ValidateUtil.getErrorMsg(ConstantManager.ERR_NOT_DATA_CORRECT));
			return JSON;
		}
		try {
			staff = getStaffByCurrentUser();
			if (staff == null || staff.getShop() == null || staff.getStaffType() == null) {
				result.put("errMsg", ValidateUtil.getErrorMsg(ConstantManager.ERR_NOT_PERMISSION, ""));
				return JSON;
			}

			PromotionProgram pro = promotionProgramMgr.getPromotionProgramById(promotionId);
			if (pro == null) {
				result.put("errMsg", ValidateUtil.getErrorMsg(ConstantManager.ERR_NOT_EXIST_DB, "CTKM"));
				return JSON;
			}
			if (!ActiveType.WAITING.equals(pro.getStatus())) {
				result.put("errMsg", Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "promotion.program.incorrect"));
				return JSON;
			}

			Shop sh = shopMgr.getShopById(shopId);
			if (sh == null) {
				result.put("errMsg", ValidateUtil.getErrorMsgQuickly(ConstantManager.ERR_NOT_EXIST_DB, false, "catalog.focus.program.shop.code"));
				return JSON;
			}

			//			PromotionShopJoin parent = promotionProgramMgr.getPromotionShopJoin(sh.getParentShop().getId(), promotionId);
			//			if (parent != null) {
			//				result.put("errMsg", R.getResource("promotion.program.shop.is.added.parent"));
			//				return JSON;
			//			}

			promotionProgramMgr.deletePromotionShopMapJoin(promotionId, shopId, getLogInfoVO());
			result.put(ERROR, false);
		} catch (Exception ex) {
			LogUtility.logErrorStandard(ex, R.getResource("web.log.message.error", "vnm.web.action.program.PromotionCatalogAction.deleteShop"), createLogErrorStandard(actionStartTime));
			result.put(ERROR, true);
			result.put("errMsg", ValidateUtil.getErrorMsg(ConstantManager.ERR_SYSTEM));
		}
		return JSON;
	}

	private List<TreeGridNode<PromotionShopVO>> lstTree;

	/**
	 * Tim kiem don vi them vao CTKM
	 * 
	 * @author lacnv1
	 * @since Aug 14, 2014
	 * 
	 * @modify hunglm16
	 * @since 26/11/2015
	 * @description Bo sung phan quyen don vi
	 */
	public String searchShopOnDlg() throws Exception {
		actionStartTime = new Date();
		if (promotionId == null || promotionId <= 0) {
			return JSON;
		}
		try {
			staff = getStaffByCurrentUser();
			if (staff == null || staff.getShop() == null || staff.getStaffType() == null || currentUser == null || currentUser.getStaffRoot() == null || currentUser.getRoleToken() == null || currentUser.getShopRoot() == null) {
				result.put("errMsg", ValidateUtil.getErrorMsg(ConstantManager.ERR_NOT_PERMISSION, ""));
				return JSON;
			}
			if (id != null) {
				shopId = id;
			}
			Shop sh;
			if (shopId == null || shopId == 0) {
				shopId = currentUser.getShopRoot().getShopId();
			}
			sh = shopMgr.getShopById(shopId);
			PromotionShopMapFilter filter = new PromotionShopMapFilter();
			filter.setShopId(shopId);
			filter.setPromotionId(promotionId);
			filter.setStaffRootId(currentUser.getStaffRoot().getStaffId());
			filter.setRoleId(currentUser.getRoleToken().getRoleId());
			filter.setShopRootId(currentUser.getShopRoot().getShopId());
			filter.setShopCode(code);
			filter.setShopName(name);
			List<PromotionShopVO> lst = promotionProgramMgr.getShopForPromotionProgram(filter);
			lstTree = new ArrayList<TreeGridNode<PromotionShopVO>>();
			if (id == null) {
				PromotionShopVO vo;
				int i;
				int sz = lst.size();
				String state;

				if (StringUtil.isNullOrEmpty(code) && StringUtil.isNullOrEmpty(name)) {
					vo = new PromotionShopVO();
					vo.setId(sh.getId());
					vo.setShopCode(sh.getShopCode());
					vo.setShopName(sh.getShopName());
					PromotionShopJoin promotionShopJoin = promotionProgramMgr.getPromotionShopJoin(sh.getId(), promotionId);
					if (promotionShopJoin != null) {
						vo.setIsExists(1);
						vo.setQuantity(promotionShopJoin.getQuantityMax() == null ? null : BigDecimal.valueOf(promotionShopJoin.getQuantityMax()));
						vo.setAmountMax(promotionShopJoin.getAmountMax());
						vo.setNumMax(promotionShopJoin.getNumMax());
					} else {
						vo.setIsExists(0);
					}
					if (ShopSpecificType.NPP.equals(sh.getType().getSpecificType())) {
						vo.setIsNPP(1);
					} else {
						vo.setIsNPP(0);
					}
					state = ConstantManager.JSTREE_STATE_CLOSE;
					i = 0;
				} else {
					vo = lst.get(0);
					state = ConstantManager.JSTREE_STATE_OPEN;
					i = 1;
				}

				TreeGridNode<PromotionShopVO> node = new TreeGridNode<PromotionShopVO>();
				node.setNodeId(vo.getId().toString());
				node.setAttr(vo);
				node.setState(ConstantManager.JSTREE_STATE_OPEN);
				node.setText(vo.getShopCode() + " - " + vo.getShopName());
				List<TreeGridNode<PromotionShopVO>> chidren = new ArrayList<TreeGridNode<PromotionShopVO>>();
				node.setChildren(chidren);
				lstTree.add(node);

				if (lst == null || lst.size() == 0) {
					vo.setIsExists(0);
					return JSON;
				}

				// Tao cay			
				TreeGridNode<PromotionShopVO> tmp;
				TreeGridNode<PromotionShopVO> tmp2;
				for (; i < sz; i++) {
					vo = lst.get(i);

					if (vo.getParentId() == null) {
						continue;
					}

					tmp2 = getNodeFromTree(lstTree, vo.getParentId().toString());
					if (tmp2 != null) {
						tmp = new TreeGridNode<PromotionShopVO>();
						tmp.setNodeId(vo.getId().toString());
						tmp.setAttr(vo);
						if (1 == vo.getIsChildren()) {
							tmp.setState(state);
						} else {
							tmp.setState(ConstantManager.JSTREE_STATE_LEAF);
						}
						tmp.setText(vo.getShopCode() + " - " + vo.getShopName());

						if (tmp2.getChildren() == null) {
							tmp2.setChildren(new ArrayList<TreeGridNode<PromotionShopVO>>());
						}
						tmp2.getChildren().add(tmp);
					}
				}
			} else {
				TreeGridNode<PromotionShopVO> tmp;
				PromotionShopVO vo;
				// Tao cay truong hop khong open full
				for (int i = 0, sz = lst.size(); i < sz; i++) {
					vo = lst.get(i);
					tmp = new TreeGridNode<PromotionShopVO>();
					tmp.setNodeId(vo.getId().toString());
					tmp.setAttr(vo);
					if (1 == vo.getIsChildren()) {
						if (ActiveType.RUNNING.getValue().equals(checkOpenFullNode)) {
							tmp.setState(ConstantManager.JSTREE_STATE_OPEN);
							filter.setShopId(vo.getId());
							if (ActiveType.RUNNING.getValue().equals(checkOpenFullNode)) {
								filter.setIsGetFullChild(true);
							}
							List<PromotionShopVO> lstTmp = promotionProgramMgr.getShopForPromotionProgram(filter);
							tmp.setChildren(this.getChildShopNewTreeVO(tmp.getAttr(), lstTmp));
						} else {
							tmp.setState(ConstantManager.JSTREE_STATE_CLOSE);
						}
						//tmp.setState(ConstantManager.JSTREE_STATE_CLOSE);
					} else {
						tmp.setState(ConstantManager.JSTREE_STATE_LEAF);
					}
					tmp.setText(vo.getShopCode() + " - " + vo.getShopName());
					lstTree.add(tmp);
				}
			}
		} catch (Exception ex) {
			LogUtility.logErrorStandard(ex, R.getResource("web.log.message.error", "vnm.web.action.program.PromotionCatalogAction.searchShopOnDlg"), createLogErrorStandard(actionStartTime));
			result.put("errMsg", ValidateUtil.getErrorMsg(ConstantManager.ERR_SYSTEM));
			result.put(ERROR, true);
		}
		return JSON;
	}

	private List<TreeGridNode<PromotionShopVO>> getChildShopNewTreeVO(PromotionShopVO shopTmp, List<PromotionShopVO> lst) {
		List<TreeGridNode<PromotionShopVO>> res = new ArrayList<TreeGridNode<PromotionShopVO>>();
		for (PromotionShopVO shopVO : lst) {
			if (shopVO.getParentId() != null && shopTmp.getId().compareTo(shopVO.getParentId()) == 0) {
				TreeGridNode<PromotionShopVO> tmp = new TreeGridNode<PromotionShopVO>();
				tmp.setNodeId(shopVO.getId().toString());
				tmp.setAttr(shopVO);
				tmp.setState(ConstantManager.JSTREE_STATE_OPEN);
				tmp.setText(shopVO.getShopCode() + " - " + shopVO.getShopName());
				res.add(tmp);
			}
		}
		for (TreeGridNode<PromotionShopVO> childrenVO : res) {
			childrenVO.setChildren(this.getChildShopNewTreeVO(childrenVO.getAttr(), lst));
			if (childrenVO.getChildren() == null || childrenVO.getChildren().size() == 0) {
				childrenVO.setState(ConstantManager.JSTREE_STATE_LEAF);
			}
		}
		return res;
	}

	/**
	 * Them don vi vao CTKM
	 * 
	 * @author lacnv1
	 * @since Aug 20, 2014
	 */
	public String addPromotionShop() {
		List<PromotionShopMap> lstCheckPromotionShopMap = new ArrayList<PromotionShopMap>();
		List<PromotionShopJoin> lstCheckPromotionShopJoin = new ArrayList<PromotionShopJoin>();
		actionStartTime = new Date();
		resetToken(result);
		result.put(ERROR, true);
		if (promotionId == null || promotionId < 1 || lstQtt == null || lstQtt.size() == 0 || lstId == null || lstId.size() == 0 || lstId.size() != lstQtt.size()) {
			result.put("errMsg", ValidateUtil.getErrorMsg(ConstantManager.ERR_NOT_DATA_CORRECT));
			return JSON;
		}
		try {
			staff = getStaffByCurrentUser();
			if (staff == null || staff.getShop() == null || staff.getStaffType() == null || currentUser == null || currentUser.getShopRoot() == null) {
				result.put("errMsg", ValidateUtil.getErrorMsg(ConstantManager.ERR_NOT_PERMISSION, ""));
				return JSON;
			}
			if (shopId == null || shopId == 0) {
				shopId = currentUser.getShopRoot().getShopId();
			}
			// kiem tra don vi, don vi con da thuoc CTKM
			List<PromotionShopVO> lstTmp = promotionProgramMgr.getListShopInPromotion(promotionId, lstId, true);
			if (lstTmp != null && lstTmp.size() > 0) {
				String msg = "";
				for (PromotionShopVO vo : lstTmp) {
					msg += (", " + vo.getShopCode());
				}
				msg = msg.replaceFirst(", ", "");
				result.put("errMsg", Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "catalog.program.promotion.shop.map.exists", msg));
				return JSON;
			}
			lstTmp = promotionProgramMgr.getListShopInPromotionShopJoin(promotionId, lstId, true);
			if (lstTmp != null && lstTmp.size() > 0) {
				String msg = "";
				for (PromotionShopVO vo : lstTmp) {
					msg += (", " + vo.getShopCode());
				}
				msg = msg.replaceFirst(", ", "");
				result.put("errMsg", Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "catalog.program.promotion.shop.map.exists", msg));
				return JSON;
			}
			lstTmp = null;
			PromotionProgram pro = promotionProgramMgr.getPromotionProgramById(promotionId);
			if (pro == null) {
				result.put("errMsg", ValidateUtil.getErrorMsg(ConstantManager.ERR_NOT_EXIST_DB, "CTKM"));
				return JSON;
			}
			if (!ActiveType.WAITING.equals(pro.getStatus()) && !ActiveType.RUNNING.equals(pro.getStatus())) {
				result.put("errMsg", Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "promotion.program.incorrect"));
				return JSON;
			}
			List<Long> lstShopId = new ArrayList<Long>();
			List<PromotionShopMap> lstPromotionShopMap = new ArrayList<PromotionShopMap>();
			List<PromotionShopJoin> lstPromotionShopJoin = new ArrayList<PromotionShopJoin>();
			lstCheckPromotionShopMap = promotionProgramMgr.getListPromotionChildShopMapWithShopAndPromotionProgram(shopId, promotionId);
			lstCheckPromotionShopJoin = promotionProgramMgr.getListPromotionChildShopJoinWithShopAndPromotionProgram(shopId, promotionId);
			for (int m = 0, sizelst = lstCheckPromotionShopMap.size(); m < sizelst; m++) {
				PromotionShopMap item = lstCheckPromotionShopMap.get(m);
				if (item != null && item.getShop() != null) {
					lstShopId.add(item.getShop().getId());
				}
			}
			for (int m = 0, sizelst = lstCheckPromotionShopJoin.size(); m < sizelst; m++) {
				PromotionShopJoin item = lstCheckPromotionShopJoin.get(m);
				if (item != null && item.getShop() != null) {
					lstShopId.add(item.getShop().getId());
				}
			}
			Shop shT = null;
			Date now = DateUtil.now();
			Long idt = null;
			for (int i = 0, sz = lstId.size(); i < sz; i++) {
				idt = lstId.get(i);
				shT = shopMgr.getShopById(idt);
				if (shT == null) {
					result.put("errMsg", ValidateUtil.getErrorMsg(ConstantManager.ERR_NOT_EXIST_DB, "DV"));
					return JSON;
				}
				if (!ActiveType.RUNNING.equals(shT.getStatus())) {
					result.put("errMsg", ValidateUtil.getErrorMsg(ConstantManager.ERR_STATUS_INACTIVE, shT.getShopCode()));
					return JSON;
				}
				if (shT.getType() != null && shT.getType().getSpecificType() != null && ShopSpecificType.NPP.getValue().equals(shT.getType().getSpecificType().getValue())) {
					PromotionShopMap psm = new PromotionShopMap();
					psm.setPromotionProgram(pro);
					psm.setShop(shT);
					psm.setStatus(ActiveType.RUNNING);
					psm.setCreateDate(now);
					psm.setCreateUser(staff.getStaffCode());
					if (DateUtil.compareTwoDate(pro.getFromDate(), commonMgr.getSysDate()) > 0) {
						psm.setFromDate(pro.getFromDate());
					} else {
						psm.setFromDate(commonMgr.getSysDate());
					}
					//psm.setFromDate(pro.getFromDate());
					psm.setToDate(pro.getToDate());
					psm.setIsQuantityMaxEdit((lstEdit.get(i) != null && lstEdit.get(i)) ? 1 : 0);
					if (lstQtt.get(i) >= ZEZO) {
						psm.setQuantityMax(lstQtt.get(i));
					}
					if (BigDecimal.ZERO.compareTo(lstAmt.get(i)) <= 0) {
						psm.setAmountMax(lstAmt.get(i));
					}
					if (BigDecimal.ZERO.compareTo(lstNum.get(i)) <= 0) {
						psm.setNumMax(lstNum.get(i));
					}
					lstPromotionShopMap.add(psm);
				} else {
					PromotionShopJoin psj = null;
					ObjectVO<Shop> listObjectChildShop = null;
					for (int k = 0, size = lstPromotionShopJoin.size(); k < size; k++) {
						PromotionShopJoin promotionShopJoin = lstPromotionShopJoin.get(k);
						if (idt.equals(promotionShopJoin.getShop().getId())) {
							psj = promotionShopJoin;
							break;
						}
					}
					if (psj == null) {
						ShopFilter flCheck = new ShopFilter();
						flCheck.setShopId(idt);
						flCheck.setListOrgAccess(lstShopIdNPP);
						flCheck.setStrShopId(super.getStrListShopId());
						// co chon NPP thi moi luu shop chon (shop join)
						if (shopMgr.getListChildrenPromotionProgram(flCheck)) {
							psj = new PromotionShopJoin();
							psj.setShop(shT);
							psj.setPromotionProgram(pro);
							psj.setStatus(ActiveType.RUNNING);
							psj.setCreateDate(now);
							psj.setCreateUser(staff.getStaffCode());
							psj.setFromDate(pro.getFromDate());
							psj.setToDate(pro.getToDate());
							// lay ds shop con
							lstShopId.add(idt);
							ShopFilter filter = new ShopFilter();
							filter.setShopId(idt);
							filter.setStatus(ActiveType.RUNNING);
							filter.setLstNotInId(lstShopId); // khong lay shop da co
							filter.setNotSpecType(ShopSpecificType.NPP); // khong lay NPP lam shop join
							filter.setStrShopId(super.getStrListShopId());
							listObjectChildShop = shopMgr.getListChildShop(filter);
						} else {
							continue;
						}
					}
					//psj.setIsQuantityMaxEdit((lstEdit.get(i) != null && lstEdit.get(i)) ? 1 : 0);
					if (lstQtt.get(i) >= ZEZO) {
						psj.setQuantityMax(lstQtt.get(i));
					}
					if (BigDecimal.ZERO.compareTo(lstAmt.get(i)) <= 0) {
						psj.setAmountMax(lstAmt.get(i));
					}
					if (BigDecimal.ZERO.compareTo(lstNum.get(i)) <= 0) {
						psj.setNumMax(lstNum.get(i));
					}
					lstPromotionShopJoin.add(psj);
					// tao theo shop con
					if (listObjectChildShop != null && listObjectChildShop.getLstObject() != null && listObjectChildShop.getLstObject().size() > 0) {
						List<Shop> listChildShop = listObjectChildShop.getLstObject();
						ShopFilter filterCheck = new ShopFilter();
						for (int k = 0, size = listChildShop.size(); k < size; k++) {
							Shop childShop = listChildShop.get(k);
							if (childShop != null) {
								filterCheck.setShopId(childShop.getId());
								filterCheck.setListOrgAccess(lstShopIdNPP);
								filterCheck.setStrShopId(super.getStrListShopId());
								if (shopMgr.getListChildrenPromotionProgram(filterCheck)) {
									PromotionShopJoin shopJoin = new PromotionShopJoin();
									shopJoin.setShop(childShop);
									shopJoin.setPromotionProgram(pro);
									shopJoin.setStatus(ActiveType.RUNNING);
									shopJoin.setCreateDate(now);
									shopJoin.setCreateUser(staff.getStaffCode());
									shopJoin.setFromDate(pro.getFromDate());
									shopJoin.setToDate(pro.getToDate());
									lstPromotionShopJoin.add(shopJoin);
								}
							}
						}
					}
				}
			}
			lstCheckPromotionShopMap.addAll(lstPromotionShopMap);
			lstCheckPromotionShopJoin.addAll(lstPromotionShopJoin);
			String error = validatePromotionShop(lstCheckPromotionShopMap, lstCheckPromotionShopJoin);
			if (!StringUtil.isNullOrEmpty(error)) {
				result.put("errMsg", error);
				return JSON;
			}
			promotionProgramMgr.addPromotionShop(lstPromotionShopMap, lstPromotionShopJoin, getLogInfoVO());
			result.put(ERROR, false);
		} catch (Exception ex) {
			LogUtility.logErrorStandard(ex, R.getResource("web.log.message.error", "vnm.web.action.program.PromotionCatalogAction.addPromotionShop"), createLogErrorStandard(actionStartTime));
			result.put("errMsg", ValidateUtil.getErrorMsg(ConstantManager.ERR_SYSTEM));
			result.put(ERROR, true);
			return JSON;
		} finally {
			lstCheckPromotionShopMap.clear();
			lstCheckPromotionShopMap = null;
			lstCheckPromotionShopJoin.clear();
			lstCheckPromotionShopJoin = null;
		}
		return JSON;
	}

	/**
	 * Xuat excel ds don vi thuoc CTKM
	 * 
	 * @author lacnv1
	 * @since Aug 20, 2014
	 */
	public String exportPromotionShop() throws Exception {
		actionStartTime = new Date();
		if (promotionId == null || promotionId <= 0) {
			result.put(ERROR, false);
			result.put("hasData", false);
			return JSON;
		}
		InputStream inputStream = null;
		OutputStream os = null;
		try {
			result.put(ERROR, true);
			staff = getStaffByCurrentUser();
			if (staff == null || staff.getShop() == null || staff.getStaffType() == null) {
				result.put("errMsg", ValidateUtil.getErrorMsg(ConstantManager.ERR_NOT_PERMISSION, ""));
				return JSON;
			}

			String reportToken = retrieveReportToken(reportCode);
			if (vnm.web.utils.StringUtil.isNullOrEmpty(reportToken)) {
				result.put(ERROR, true);
				result.put("errMsg", Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "report.invalid.token"));
				return JSON;
			}

			PromotionShopMapFilter filter = new PromotionShopMapFilter();
			filter.setProgramId(promotionId);
			filter.setStatus(ActiveType.RUNNING);
			filter.setParentShopId(currentUser.getShopRoot().getShopId());
			filter.setShopCode(code);
			filter.setShopName(name);
			filter.setQuantityMax(quantity);
			List<PromotionShopMapVO> lst = promotionProgramMgr.getPromotionShopMapVOByFilter(filter, null);
			if (lst == null || lst.size() == 0) {
				result.put(ERROR, false);
				result.put("hasData", false);
				return JSON;
			}
			String folder = ServletActionContext.getServletContext().getRealPath("/") + Configuration.getExcelTemplatePathCatalog();
			StringBuilder sb = new StringBuilder(folder).append(ConstantManager.TEMPLATE_PROMOTION_SHOP_MAP_EXPORT).append(FileExtension.XLS.getValue());
			String templateFileName = sb.toString();
			templateFileName = templateFileName.replace('/', File.separatorChar);

			sb = new StringBuilder(ConstantManager.TEMPLATE_PROMOTION_SHOP_MAP_EXPORT).append(DateUtil.toDateString(DateUtil.now(), DateUtil.DATE_FORMAT_EXCEL_FILE)).append(FileExtension.XLS.getValue());
			String outputName = sb.toString();
			sb = null;
			String exportFileName = Configuration.getStoreRealPath() + outputName;

			Map<String, Object> beans = new HashMap<String, Object>();

			beans.put("hasData", 1);
			beans.put("lstShop", lst);

			inputStream = new BufferedInputStream(new FileInputStream(templateFileName));
			XLSTransformer transformer = new XLSTransformer();
			Workbook resultWorkbook = transformer.transformXLS(inputStream, beans);
			os = new BufferedOutputStream(new FileOutputStream(exportFileName));
			resultWorkbook.write(os);
			os.flush();
			result.put(ERROR, false);
			String outputPath = Configuration.getExportExcelPath() + outputName;
			result.put(REPORT_PATH, outputPath);
			MemcachedUtils.putValueToMemcached(reportToken, outputPath, retrieveReportMemcachedTimeout());
		} catch (Exception ex) {
			LogUtility.logErrorStandard(ex, R.getResource("web.log.message.error", "vnm.web.action.program.PromotionCatalogAction.exportPromotionShop"), createLogErrorStandard(actionStartTime));
			result.put("errMsg", ValidateUtil.getErrorMsg(ConstantManager.ERR_SYSTEM));
			result.put(ERROR, true);
		} finally {
			if (inputStream != null) {
				IOUtils.closeQuietly(inputStream);
			}
			if (os != null) {
				IOUtils.closeQuietly(os);
			}
		}
		return JSON;
	}

	/**
	 * Import don vi tu excel
	 * 
	 * @author lacnv1
	 * @since Aug 10, 2014
	 * 
	 * @modify hunglm16
	 * @since 14/09/2015
	 * @description Them So tien, So luong va Hotfix
	 */
	public String importPromotionShop() throws Exception {
		List<PromotionShopMap> lstCheckPromotionShopMap = new ArrayList<PromotionShopMap>();
		List<PromotionShopJoin> lstCheckPromotionShopJoin = new ArrayList<PromotionShopJoin>();
		actionStartTime = new Date();
		resetToken(result);
		isError = true;
		try {
			if (currentUser == null || currentUser.getShopRoot() == null) {
				isError = true;
				errMsg = ValidateUtil.getErrorMsg(ConstantManager.ERR_NOT_PERMISSION);
				return SUCCESS;
			}
			PromotionProgram promotionProgram = promotionProgramMgr.getPromotionProgramById(promotionId);
			if (promotionProgram == null) {
				isError = true;
				errMsg = ValidateUtil.getErrorMsg(ConstantManager.ERR_NOT_EXIST_DB, "CTKM");
				return SUCCESS;
			}
			/*if (!ActiveType.WAITING.equals(promotionProgram.getStatus())) {
				isError = true;
				errMsg = Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "promotion.program.incorrect");
				return SUCCESS;
			}*/
			totalItem = 0;
			String message;
			lstView = new ArrayList<CellBean>();
			typeView = true;
			List<CellBean> lstFails = new ArrayList<CellBean>();
			List<List<String>> lstData = getExcelDataEx(excelFile, excelFileContentType, errMsg, 4);
			if (StringUtil.isNullOrEmpty(errMsg) && lstData != null && lstData.size() > 0) {
				Date currentSysDate = commonMgr.getSysDate();
				PromotionShopMap promotionShopMap;
				//				PromotionCustomerMap promotionCustomerMap = null;
				List<String> row;
				Shop shop = null;
				//List<String> parentShopCode = null;
				//Customer c = null;
				boolean isEdit;
				//				boolean isCreateCustomer;
				PromotionShopMap psm = null;
				LogInfoVO logInfo = getLogInfoVO();
				String msg;
				String value;
				for (int i = 0, size = lstData.size(); i < size; i++) {
					if (lstData.get(i) != null && lstData.get(i).size() > 0) {
						boolean flagContinue = true;
						//Bo qua cac dong trong
						for (int j = 0; j < 4; j++) {
							if (!StringUtil.isNullOrEmpty(lstData.get(i).get(j))) {
								flagContinue = false;
							}
						}
						if (flagContinue) {
							continue;
						}
						message = "";
						//						value = "";
						totalItem++;
						row = lstData.get(i);
						isEdit = false;
						//						isCreateCustomer = false;
						promotionShopMap = new PromotionShopMap();
						promotionShopMap.setPromotionProgram(promotionProgram);
						if (DateUtil.compareTwoDate(promotionProgram.getFromDate(), commonMgr.getSysDate()) > 0) {
							promotionShopMap.setFromDate(promotionProgram.getFromDate());
						} else {
							promotionShopMap.setFromDate(commonMgr.getSysDate());
						}
						//promotionShopMap.setFromDate(promotionProgram.getFromDate());
						promotionShopMap.setToDate(promotionProgram.getToDate());
						if (row.size() > 0) {
							value = row.get(0);
							if (!StringUtil.isNullOrEmpty(value)) {
								shop = shopMgr.getShopByCode(value);
								if (shop == null) {
									message += ValidateUtil.getErrorMsgQuickly(ConstantManager.ERR_NOT_EXIST_DB, true, "catalog.focus.program.shop.code");
								} else if (!ActiveType.RUNNING.equals(shop.getStatus())) {
									message += ValidateUtil.getErrorMsgQuickly(ConstantManager.ERR_STATUS_INACTIVE, true, "catalog.focus.program.shop.code");
								} else if (!checkShopPermission(shop.getShopCode())) {
									message += ValidateUtil.getErrorMsg(ConstantManager.ERR_INVALID_SHOP, shop.getShopCode(), currentUser.getUserName()) + "\n";
								} else if (shop.getType() == null || shop.getType().getSpecificType() == null || !ShopSpecificType.NPP.getValue().equals(shop.getType().getSpecificType().getValue())) {
									//Don vi khong phai la NPP
									message += R.getResource("common.cms.shop.islevel5.undefined");
								}
							} else {
								message += R.getResource("common.missing.not.in.system.p", R.getResource("catalog.focus.program.shop.code")) + "\n";
							}
							if (StringUtil.isNullOrEmpty(message)) {
								boolean flag1 = promotionProgramMgr.isExistChildJoinProgram(shop != null ? shop.getShopCode() : null, promotionId);
								if (flag1) {
									message += Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "catalog.promotion.program.child.shop.is.exists") + "\n";
								}
							}

							if (shop != null) {
								psm = promotionProgramMgr.getPromotionShopMap(shop.getId(), promotionProgram.getId());
							}

							if (StringUtil.isNullOrEmpty(message)) {
								promotionShopMap.setShop(shop);
								if (psm != null) {
									isEdit = true;
									promotionShopMap = psm;
								}
							}
						}
						// So suat NPP
						value = row.get(1);
						msg = "";
						if (!StringUtil.isNullOrEmpty(value)) {
							if (value.trim().length() > maxlengthNumber || !StringUtil.isNumberInt(value)) {
								msg = R.getResource("catalog.promotion.import.khtg.ctkm.value.number.integer", R.getResource("catalog.suatNPP.code"));
							} else {
								if (Integer.valueOf(value) > 0) {
									promotionShopMap.setQuantityMax(Integer.valueOf(value));
								} else {
									msg = Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "common.not.negative", Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "catalog.suatNPP.code"));
								}
							}
						} else if (promotionShopMap != null) {
							promotionShopMap.setQuantityMax(null);
						}
						if (!StringUtil.isNullOrEmpty(msg)) {
							message += msg + "\n";
						}

						//So tien
						value = row.get(2);
						msg = "";
						if (!StringUtil.isNullOrEmpty(value)) {
							if (value.trim().length() > maxlengthNumber || !StringUtil.isNumberInt(value)) {
								msg = R.getResource("catalog.promotion.import.khtg.ctkm.value.number.integer", R.getResource("catalog.promotion.import.khtg.ctkm.clmn.amount"));
							} else {
								if (Integer.valueOf(value) > 0) {
									promotionShopMap.setAmountMax(BigDecimal.valueOf(Long.valueOf(value)));
								} else {
									msg = Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "common.not.negative", Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "catalog.promotion.import.khtg.ctkm.clmn.amount"));
								}
							}
						} else if (promotionShopMap != null) {
							promotionShopMap.setAmountMax(null);
						}
						if (!StringUtil.isNullOrEmpty(msg)) {
							message += msg + "\n";
						}
						//So luong
						value = row.get(3);
						msg = "";
						if (!StringUtil.isNullOrEmpty(value)) {
							if (value.trim().length() > maxlengthNumber || !StringUtil.isNumberInt(value)) {
								msg = R.getResource("catalog.promotion.import.khtg.ctkm.value.number.integer", R.getResource("catalog.promotion.import.khtg.ctkm.clmn.quantity"));
							} else {
								if (Integer.valueOf(value) > 0) {
									promotionShopMap.setNumMax(BigDecimal.valueOf(Long.valueOf(value)));
								} else {
									msg = Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "common.not.negative", Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "catalog.promotion.import.khtg.ctkm.clmn.quantity"));
								}
							}
						} else if (promotionShopMap != null) {
							promotionShopMap.setNumMax(null);
						}
						if (!StringUtil.isNullOrEmpty(msg)) {
							message += msg + "\n";
						}
						if (StringUtil.isNullOrEmpty(message)) {
							if (lstCheckPromotionShopMap.size() == 0) {
								lstCheckPromotionShopMap = promotionProgramMgr.getListPromotionChildShopMapWithShopAndPromotionProgram(currentUser.getShopRoot().getShopId(), promotionId);
							}
							if (lstCheckPromotionShopJoin.size() == 0) {
								lstCheckPromotionShopJoin = promotionProgramMgr.getListPromotionChildShopJoinWithShopAndPromotionProgram(currentUser.getShopRoot().getShopId(), promotionId);
							}
							if (isEdit) {
								for (PromotionShopMap item : lstCheckPromotionShopMap) {
									if (item.getId() != null && item.getId().equals(promotionShopMap.getId())) {
										lstCheckPromotionShopMap.remove(item);
										break;
									}
								}
							}
							lstCheckPromotionShopMap.add(promotionShopMap);
							String error = validatePromotionShop(lstCheckPromotionShopMap, lstCheckPromotionShopJoin);
							if (!StringUtil.isNullOrEmpty(error)) {
								message += error + "\n";
							}
						}
						if (StringUtil.isNullOrEmpty(message)) {
							if (isEdit) {
								promotionShopMap.setUpdateDate(currentSysDate);
								promotionShopMap.setCreateUser(currentUser.getUserName());
								promotionProgramMgr.updatePromotionShopMap(promotionShopMap, logInfo);
								//									if (promotionCustomerMap != null && promotionCustomerMap.getCustomer() != null) {
								//										promotionCustomerMap.setShop(promotionShopMap.getShop());
								//										if (isCreateCustomer) {
								//											promotionCustomerMap.setCreateDate(currentSysDate);
								//											promotionCustomerMap.setCreateUser(currentUser.getUserName());
								//											promotionProgramMgr.createPromotionCustomerMap(promotionCustomerMap, logInfo);
								//										} else {
								//											promotionCustomerMap.setUpdateDate(currentSysDate);
								//											promotionCustomerMap.setUpdateUser(currentUser.getUserName());
								//											promotionProgramMgr.updatePromotionCustomerMap(promotionCustomerMap, logInfo);
								//										}
								//									}
							} else {
								if (ActiveType.WAITING.equals(promotionProgram.getStatus()) || ActiveType.RUNNING.equals(promotionProgram.getStatus())) {
									promotionShopMap.setCreateDate(currentSysDate);
									promotionShopMap.setCreateUser(currentUser.getUserName());
									promotionProgramMgr.createPromotionShopMap(promotionShopMap, logInfo);
									//									PromotionShopMap s = promotionProgramMgr.createPromotionShopMap(promotionShopMap, logInfo);
									//										if (promotionCustomerMap != null && promotionCustomerMap.getCustomer() != null) {
									//											promotionCustomerMap.setPromotionShopMap(s);
									//											promotionCustomerMap.setCreateDate(currentSysDate);
									//											promotionCustomerMap.setCreateUser(currentUser.getUserName());
									//											promotionCustomerMap.setShop(promotionShopMap.getShop());
									//											promotionProgramMgr.createPromotionCustomerMap(promotionCustomerMap, logInfo);
									//										}
								}
							}
							isError = false;
						} else {
							lstFails.add(StringUtil.addFailBean(row, message));
						}
						typeView = false;
					}
				}
				// Export error
				getOutputFailExcelFile(lstFails, ConstantManager.TEMPLATE_CATALOG_PP_SHOP_FAIL);
			}
			if (StringUtil.isNullOrEmpty(errMsg)) {
				isError = false;
			}
		} catch (BusinessException ex) {
			LogUtility.logErrorStandard(ex, R.getResource("web.log.message.error", "vnm.web.action.program.PromotionCatalogAction.importPromotionShop"), createLogErrorStandard(actionStartTime));
			isError = true;
			errMsg = ValidateUtil.getErrorMsg(ConstantManager.ERR_SYSTEM);
		}
		lstCheckPromotionShopMap.clear();
		//		lstCheckPromotionShopMap = null;
		lstCheckPromotionShopJoin.clear();
		//		lstCheckPromotionShopJoin = null;
		return SUCCESS;
	}

	/**
	 * Lay danh sach thuoc tinh da co trong CTKM
	 * 
	 * @author lacnv1
	 * @since Aug 22, 2014
	 */
	public String loadAppliedAttributes() throws Exception {
		actionStartTime = new Date();
		if (promotionId != null && promotionId > 0) {
			try {
				List<PromotionCustAttrVO> lst = promotionProgramMgr.getListPromotionCustAttrVOAlreadySet(null, promotionId);
				CustomerAttribute attribute;
				AttributeColumnType attColType;
				PromotionCustAttVO2 voTmp;
				List<PromotionCustAttVO2> lstTmp = promotionProgramMgr.getListPromotionCustAttVOValue(promotionId);
				List<PromotionCustAttVO2> lstTmpDetail = promotionProgramMgr.getListPromotionCustAttVOValueDetail(promotionId);
				ArrayList<Object> lstData;
				List<AttributeDetailVO> lstAttDetail;
				AttributeDetailVO voDetail;
				int j, sz;
				int k, szk;
				if (lst != null && lst.size() > 0) {
					for (PromotionCustAttrVO vo : lst) {
						if (vo.getObjectType() == AUTO_ATTRIBUTE) {
							attribute = customerAttributeMgr.getCustomerAttributeById(vo.getObjectId());
							attColType = attribute.getValueType();
							vo.setValueType(attColType);//setValueType
							if (attColType == AttributeColumnType.CHARACTER || attColType == AttributeColumnType.NUMBER || attColType == AttributeColumnType.DATE_TIME) {
								//Value cua thuoc tinh dong type (1,2,3)
								if (lstTmp != null && lstTmp.size() > 0) {
									if (attColType == AttributeColumnType.CHARACTER) {
										for (j = 0, sz = lstTmp.size(); j < sz; j++) {
											voTmp = lstTmp.get(j);
											if (voTmp.getAttributeId().equals(vo.getObjectId())) {
												lstData = new ArrayList<Object>();
												lstData.add(voTmp.getFromValue());
												vo.setListData(lstData);//setListData
											}
										}
									} else if (attColType == AttributeColumnType.NUMBER) {
										//										valueType=2;
										for (j = 0, sz = lstTmp.size(); j < sz; j++) {
											voTmp = lstTmp.get(j);
											if (voTmp.getAttributeId().equals(vo.getObjectId())) {
												lstData = new ArrayList<Object>();
												lstData.add(voTmp.getFromValue());
												lstData.add(voTmp.getToValue());
												vo.setListData(lstData);//setListData
											}
										}
									} else if (attColType == AttributeColumnType.DATE_TIME) {
										//										valueType=3;
										for (j = 0, sz = lstTmp.size(); j < sz; j++) {
											voTmp = lstTmp.get(j);
											if (voTmp.getAttributeId().equals(vo.getObjectId())) {
												lstData = new ArrayList<Object>();
												lstData.add(DateUtil.convertFormatStrFromAtt(voTmp.getFromValue()));
												lstData.add(DateUtil.convertFormatStrFromAtt(voTmp.getToValue()));
												vo.setListData(lstData);//setListData
											}
										}
									}
								}
							} else if (attColType == AttributeColumnType.CHOICE || attColType == AttributeColumnType.MULTI_CHOICE) {
								//Value cua thuoc tinh dong type (4,5)
								if (lstTmpDetail != null && lstTmpDetail.size() > 0) {
									lstAttDetail = null;
									for (j = 0, sz = lstTmpDetail.size(); j < sz; j++) {
										voTmp = lstTmpDetail.get(j);
										if (voTmp.getAttributeId().equals(attribute.getId())) {
											if (lstAttDetail == null) {
												lstAttDetail = promotionProgramMgr.getListPromotionCustAttVOCanBeSet(attribute.getId());
											}
											if (lstAttDetail != null && lstAttDetail.size() > 0) {
												for (k = 0, szk = lstAttDetail.size(); k < szk; k++) {
													voDetail = lstAttDetail.get(k);
													if (voDetail.getEnumId().equals(voTmp.getAttributeEnumId())) {
														voDetail.setChecked(true);
														break;
													}
												}
											}
										}
									}
									vo.setListData(lstAttDetail);//setListData
								}
							}
						} else if (vo.getObjectType() == CUSTOMER_TYPE) {
							List<ChannelTypeVO> listChannelTypeVO = promotionProgramMgr.getListChannelTypeVO();
							List<ChannelTypeVO> listSelectedChannelTypeVO = promotionProgramMgr.getListChannelTypeVOAlreadySet(null, promotionId);
							if (listChannelTypeVO != null && listChannelTypeVO.size() > 0 && listSelectedChannelTypeVO != null && listSelectedChannelTypeVO.size() > 0) {
								for (ChannelTypeVO channelTypeVO : listChannelTypeVO) {
									for (ChannelTypeVO channelTypeVO1 : listSelectedChannelTypeVO) {
										if (channelTypeVO1.getIdChannelType().equals(channelTypeVO.getIdChannelType())) {
											channelTypeVO.setChecked(true);
											break;
										}
									}
								}
							}
							vo.setListData(listChannelTypeVO);//setListData
						} else if (vo.getObjectType() == SALE_LEVEL) {
							List<SaleCatLevelVO> listSelectedSaleCatLevelVO = promotionProgramMgr.getListSaleCatLevelVOByIdProAlreadySetSO(null, promotionId);
							vo.setListData(listSelectedSaleCatLevelVO);
							List<ProductInfoVO> listProductInfoVO = promotionProgramMgr.getListProductInfoVO();
							if (listProductInfoVO != null && listProductInfoVO.size() > 0) {
								for (ProductInfoVO productInfoVO : listProductInfoVO) {
									List<SaleCatLevelVO> listSaleCatLevelVO = promotionProgramMgr.getListSaleCatLevelVOByIdPro(productInfoVO.getIdProductInfoVO());
									productInfoVO.setListSaleCatLevelVO(listSaleCatLevelVO);
								}
								vo.setListProductInfoVO(listProductInfoVO);
							} else {
								vo.setListProductInfoVO(new ArrayList<ProductInfoVO>());
							}
						}
					}

					result.put("list", lst);//Neu ko co list de put thi nho new 1 arrayList roi put a.
				}
			} catch (Exception ex) {
				LogUtility.logErrorStandard(ex, R.getResource("web.log.message.error", "vnm.web.action.program.PromotionCatalogAction.loadAppliedAttributes"), createLogErrorStandard(actionStartTime));
				result.put("errMsg", ValidateUtil.getErrorMsg(ConstantManager.ERR_SYSTEM));
				result.put(ERROR, true);
				return JSON;
			}
		}
		return JSON;
	}

	/**
	 * Lay danh sach gia tri cho thuoc tinh KH
	 * 
	 * @author lacnv1
	 * @since Aug 22, 2014
	 */
	public String getAllDataForAttributes() throws Exception {
		actionStartTime = new Date();
		try {
			if (lstObjectType != null && lstObjectType.size() > 0 && lstId != null && lstId.size() > 0 && lstObjectType.size() == lstId.size()) { //lstId = listObjectId
				List<PromotionCustAttrVO> lst = new ArrayList<PromotionCustAttrVO>();
				PromotionCustAttrVO vo;
				Integer objectType;
				Long objectId;
				CustomerAttribute attribute;
				AttributeColumnType attColType;
				List<AttributeDetailVO> lstAttDetail;
				for (int i = 0; i < lstObjectType.size(); i++) {
					objectType = lstObjectType.get(i);
					objectId = lstId.get(i);
					if (objectType != null) {
						vo = new PromotionCustAttrVO();
						vo.setObjectType(objectType);
						vo.setObjectId(objectId);
						if (objectType.equals(AUTO_ATTRIBUTE)) {
							attribute = customerAttributeMgr.getCustomerAttributeById(objectId);
							if (attribute != null) {
								attColType = attribute.getValueType();
								vo.setValueType(attColType);// setValueType
								vo.setName(attribute.getName());
								// chi set du lieu cho kieu dropdownlist:
								if (attColType == AttributeColumnType.CHOICE || attColType == AttributeColumnType.MULTI_CHOICE) {
									lstAttDetail = promotionProgramMgr.getListPromotionCustAttVOCanBeSet(attribute.getId());
									vo.setListData(lstAttDetail);// setListData
								}
							}
						} else if (objectType.equals(CUSTOMER_TYPE)) {
							List<ChannelTypeVO> listChannelTypeVO = promotionProgramMgr.getListChannelTypeVO();
							vo.setListData(listChannelTypeVO);//setListData
						} else if (objectType.equals(SALE_LEVEL)) {
							List<ProductInfoVO> listProductInfoVO = promotionProgramMgr.getListProductInfoVO();
							if (listProductInfoVO != null && listProductInfoVO.size() > 0) {
								for (ProductInfoVO productInfoVO : listProductInfoVO) {
									List<SaleCatLevelVO> listSaleCatLevelVO = promotionProgramMgr.getListSaleCatLevelVOByIdPro(productInfoVO.getIdProductInfoVO());
									productInfoVO.setListSaleCatLevelVO(listSaleCatLevelVO);
								}
								vo.setListProductInfoVO(listProductInfoVO);
							}
						}

						lst.add(vo);
					} else {
						return JSON;
					}
				}
				result.put("list", lst);
			}
		} catch (Exception ex) {
			LogUtility.logErrorStandard(ex, R.getResource("web.log.message.error", "vnm.web.action.program.PromotionCatalogAction.getAllDataForAttributes"), createLogErrorStandard(actionStartTime));
			result.put("errMsg", ValidateUtil.getErrorMsg(ConstantManager.ERR_SYSTEM));
			result.put(ERROR, true);
			return JSON;
		}
		return JSON;
	}

	/**
	 * Luu thuoc tinh KH tham gia CTKM
	 * 
	 * @author lacnv1
	 * @since Aug 22, 2014
	 */
	public String savePromotionCustAtt() throws Exception {
		actionStartTime = new Date();
		resetToken(result);
		boolean error = true;
		List<PromotionCustAttUpdateVO> listPromotionCustAttUpdateVO = new ArrayList<PromotionCustAttUpdateVO>();
		try {
			PromotionProgram promotionProgram = promotionProgramMgr.getPromotionProgramById(promotionId);
			if (promotionProgram == null) {
				result.put(ERROR, error);
				result.put("errMsg", Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "system.error"));//Loi ko dung id promotionProgram
				return JSON;
			}
			if (lstId != null && lstId.size() > 0 && lstAttDataInField != null && lstAttDataInField.size() > 0) {
				Long attributeI;
				PromotionCustAttr promotionCustAttr;
				PromotionCustAttr pca;
				CustomerAttribute attribute;
				String dataI;
				AttributeColumnType attColType;
				PromotionCustAttUpdateVO promotionCustAttUpdateVO;
				for (int i = 0, sz = lstId.size(); i < sz; i++) {
					if (lstId.get(i) != null && lstId.get(i).equals(-2l)) {//-2 la customerType
						lstCustomerType = checkLstCustomerType(lstCustomerType);
						if (lstCustomerType != null && lstCustomerType.size() > 0) {
							pca = promotionProgramMgr.getPromotionCustAttrByPromotion(promotionId, 2, null);
							if (pca != null) {
								promotionCustAttr = pca;
								promotionCustAttr.setUpdateUser(currentUser.getUserName());
							} else {
								promotionCustAttr = new PromotionCustAttr();
								promotionCustAttr.setCreateUser(currentUser.getUserName());
							}
							promotionCustAttr.setPromotionProgram(promotionProgram);
							promotionCustAttr.setObjectType(2);
							promotionCustAttr.setSeq(i);//set seq theo chi so trong List.
							List<PromotionCustAttrDetail> listPromotionCustAttrDetail = new ArrayList<PromotionCustAttrDetail>();
							if (pca != null && pca.getId() != null) {
								PromotionCustAttrDetail tmpPCAD;
								Long promotionCustAttrId = pca.getId();
								for (Long chanelTypeId : lstCustomerType) {
									PromotionCustAttrDetail promotionCustAttrDetail = new PromotionCustAttrDetail();
									promotionCustAttrDetail.setObjectType(2);
									promotionCustAttrDetail.setObjectId(chanelTypeId);
									tmpPCAD = promotionProgramMgr.getPromotionCustAttrDetail(promotionCustAttrId, 2L, chanelTypeId);
									if (tmpPCAD != null) {
										promotionCustAttrDetail.setCreateUser(tmpPCAD.getCreateUser());
										promotionCustAttrDetail.setUpdateUser(currentUser.getUserName());
									} else {
										promotionCustAttrDetail.setCreateUser(currentUser.getUserName());
									}
									//									tmpPCAD = null;
									listPromotionCustAttrDetail.add(promotionCustAttrDetail);
								}
							} else {
								for (Long chanelTypeId : lstCustomerType) {
									PromotionCustAttrDetail promotionCustAttrDetail = new PromotionCustAttrDetail();
									promotionCustAttrDetail.setObjectType(2);
									promotionCustAttrDetail.setObjectId(chanelTypeId);
									promotionCustAttrDetail.setCreateUser(currentUser.getUserName());
									listPromotionCustAttrDetail.add(promotionCustAttrDetail);
								}
							}
							promotionCustAttUpdateVO = new PromotionCustAttUpdateVO();
							promotionCustAttUpdateVO.setPromotionCustAttr(promotionCustAttr);
							promotionCustAttUpdateVO.setLstPromotionCustAttrDetail(listPromotionCustAttrDetail);
							listPromotionCustAttUpdateVO.add(promotionCustAttUpdateVO);
						}
					} else if (lstId.get(i) != null && lstId.get(i).equals(-3l)) {//-3 la saleLevel
						if (lstSaleLevelCatId != null && lstSaleLevelCatId.size() > 0) {
							pca = promotionProgramMgr.getPromotionCustAttrByPromotion(promotionId, 3, null);
							if (pca != null) {
								promotionCustAttr = pca;
								promotionCustAttr.setUpdateUser(currentUser.getUserName());
							} else {
								promotionCustAttr = new PromotionCustAttr();
								promotionCustAttr.setCreateUser(currentUser.getUserName());
							}
							promotionCustAttr.setPromotionProgram(promotionProgram);
							promotionCustAttr.setObjectType(3);
							promotionCustAttr.setSeq(i);//set seq de load len cho dung thu tu
							List<PromotionCustAttrDetail> listPromotionCustAttrDetail = new ArrayList<PromotionCustAttrDetail>();
							if (pca != null && pca.getId() != null) {
								PromotionCustAttrDetail tmpPCAD;
								Long promotionCustAttrId = pca.getId();
								for (Long saleLevelCatId : lstSaleLevelCatId) {
									if (saleLevelCatId > -1L) {
										PromotionCustAttrDetail promotionCustAttrDetail = new PromotionCustAttrDetail();
										promotionCustAttrDetail.setObjectType(3);
										promotionCustAttrDetail.setObjectId(saleLevelCatId);
										tmpPCAD = promotionProgramMgr.getPromotionCustAttrDetail(promotionCustAttrId, 3L, saleLevelCatId);
										if (tmpPCAD != null) {
											promotionCustAttrDetail.setCreateUser(tmpPCAD.getCreateUser());
											promotionCustAttrDetail.setUpdateUser(currentUser.getUserName());
										} else {
											promotionCustAttrDetail.setCreateUser(currentUser.getUserName());
										}
										//										tmpPCAD = null;
										listPromotionCustAttrDetail.add(promotionCustAttrDetail);
									}
								}
							} else {
								for (Long saleLevelCatId : lstSaleLevelCatId) {
									if (saleLevelCatId > -1L) {
										PromotionCustAttrDetail promotionCustAttrDetail = new PromotionCustAttrDetail();
										promotionCustAttrDetail.setObjectType(3);
										promotionCustAttrDetail.setObjectId(saleLevelCatId);
										promotionCustAttrDetail.setCreateUser(currentUser.getUserName());
										listPromotionCustAttrDetail.add(promotionCustAttrDetail);
									}
								}
							}
							promotionCustAttUpdateVO = new PromotionCustAttUpdateVO();
							promotionCustAttUpdateVO.setPromotionCustAttr(promotionCustAttr);
							promotionCustAttUpdateVO.setLstPromotionCustAttrDetail(listPromotionCustAttrDetail);
							listPromotionCustAttUpdateVO.add(promotionCustAttUpdateVO);
						}
					} else {
						attributeI = lstId.get(i);
						attribute = customerAttributeMgr.getCustomerAttributeById(attributeI);
						//set vao entity:
						pca = promotionProgramMgr.getPromotionCustAttrByPromotion(promotionId, 1, attributeI);
						if (pca != null) {
							promotionCustAttr = pca;
							promotionCustAttr.setUpdateUser(currentUser.getUserName());
						} else {
							promotionCustAttr = new PromotionCustAttr();
							promotionCustAttr.setCreateUser(currentUser.getUserName());
						}
						promotionCustAttr.setPromotionProgram(promotionProgram);
						promotionCustAttr.setObjectType(1);
						promotionCustAttr.setObjectId(attribute.getId());
						promotionCustAttr.setSeq(i);
						//
						dataI = lstAttDataInField.get(i);
						attColType = attribute.getValueType();
						if (attColType == AttributeColumnType.CHARACTER) {
							promotionCustAttr.setFromValue(dataI);
							// set vao VO to save:
							promotionCustAttUpdateVO = new PromotionCustAttUpdateVO();
							promotionCustAttUpdateVO.setPromotionCustAttr(promotionCustAttr);
							promotionCustAttUpdateVO.setLstPromotionCustAttrDetail(null);
							listPromotionCustAttUpdateVO.add(promotionCustAttUpdateVO);
						} else if (attColType == AttributeColumnType.NUMBER) {
							//							if (arr != null && arr.length == 2) {
							//								promotionCustAttr.setFromValue(arr[0]);
							//								promotionCustAttr.setToValue(arr[1]);
							//						    }
							String[] arr = dataI.split(",");
							if (arr != null) {
								if (arr.length == 2) {
									promotionCustAttr.setFromValue(arr[0]);
									promotionCustAttr.setToValue(arr[1]);
								} else if (arr.length == 1) {
									promotionCustAttr.setFromValue(arr[0]);
									promotionCustAttr.setToValue("");
								}
							}
							// set vao VO to save:
							promotionCustAttUpdateVO = new PromotionCustAttUpdateVO();
							promotionCustAttUpdateVO.setPromotionCustAttr(promotionCustAttr);
							promotionCustAttUpdateVO.setLstPromotionCustAttrDetail(null);
							listPromotionCustAttUpdateVO.add(promotionCustAttUpdateVO);

						} else if (attColType == AttributeColumnType.DATE_TIME) {
							String[] arr = dataI.split(",");
							if (arr != null) {
								if (arr.length == 2) {
									String fromValueTmp = DateUtil.convertFormatAttFromStr(arr[0]);
									String toValueTmp = DateUtil.convertFormatAttFromStr(arr[1]);
									promotionCustAttr.setFromValue(fromValueTmp);
									promotionCustAttr.setToValue(toValueTmp);
								} else if (arr.length == 1) {
									String fromValueTmp = DateUtil.convertFormatAttFromStr(arr[0]);
									promotionCustAttr.setFromValue(fromValueTmp);
									promotionCustAttr.setToValue("");
								}
							}
							// set vao VO to save:
							promotionCustAttUpdateVO = new PromotionCustAttUpdateVO();
							promotionCustAttUpdateVO.setPromotionCustAttr(promotionCustAttr);
							promotionCustAttUpdateVO.setLstPromotionCustAttrDetail(null);
							listPromotionCustAttUpdateVO.add(promotionCustAttUpdateVO);
						} else if (attColType == AttributeColumnType.CHOICE || attColType == AttributeColumnType.MULTI_CHOICE) {
							List<PromotionCustAttrDetail> listPromotionCustAttrDetail = new ArrayList<PromotionCustAttrDetail>();
							String[] arr = dataI.split(",");
							if (pca != null && pca.getId() != null) {
								PromotionCustAttrDetail tmpPCAD;
								Long promotionCustAttrId = pca.getId();
								if (arr != null && arr.length > 0) {
									for (int k = 0, szk = arr.length; k < szk; k++) {
										PromotionCustAttrDetail promotionCustAttrDetail = new PromotionCustAttrDetail();
										promotionCustAttrDetail.setObjectType(1);
										promotionCustAttrDetail.setObjectId(Long.valueOf(arr[k]));
										tmpPCAD = promotionProgramMgr.getPromotionCustAttrDetail(promotionCustAttrId, 1L, Long.valueOf(arr[k]));
										if (tmpPCAD != null) {
											promotionCustAttrDetail.setCreateUser(tmpPCAD.getCreateUser());
											promotionCustAttrDetail.setUpdateUser(currentUser.getUserName());
										} else {
											promotionCustAttrDetail.setCreateUser(currentUser.getUserName());
										}
										//										tmpPCAD = null;
										listPromotionCustAttrDetail.add(promotionCustAttrDetail);
									}
								}
							} else {
								if (arr != null && arr.length > 0) {
									for (int k = 0, szk = arr.length; k < szk; k++) {
										PromotionCustAttrDetail promotionCustAttrDetail = new PromotionCustAttrDetail();
										promotionCustAttrDetail.setObjectType(1);
										promotionCustAttrDetail.setObjectId(Long.valueOf(arr[k]));
										promotionCustAttrDetail.setCreateUser(currentUser.getUserName());
										listPromotionCustAttrDetail.add(promotionCustAttrDetail);
									}
								}
							}
							promotionCustAttUpdateVO = new PromotionCustAttUpdateVO();
							promotionCustAttUpdateVO.setPromotionCustAttr(promotionCustAttr);
							promotionCustAttUpdateVO.setLstPromotionCustAttrDetail(listPromotionCustAttrDetail);
							listPromotionCustAttUpdateVO.add(promotionCustAttUpdateVO);
						}
					}
				}
			}

			promotionProgramMgr.createOrUpdatePromotionCustAttVO(promotionId, listPromotionCustAttUpdateVO, getLogInfoVO());
			result.put(ERROR, false);
		} catch (Exception ex) {
			LogUtility.logErrorStandard(ex, R.getResource("web.log.message.error", "vnm.web.action.program.PromotionCatalogAction.savePromotionCustAtt"), createLogErrorStandard(actionStartTime));
			result.put("errMsg", ValidateUtil.getErrorMsg(ConstantManager.ERR_SYSTEM));
			result.put(ERROR, true);
		}
		return JSON;
	}

	/**
	 * neu chon loai cha thi remove loai con
	 * 
	 * @param lstCustomerType
	 * @return
	 * @throws Exception
	 */
	public List<Long> checkLstCustomerType(List<Long> lstCustomerType) throws Exception {
		List<Long> removelist = new ArrayList<Long>();
		if (lstCustomerType != null && lstCustomerType.size() > 0) {
			String lstCustomerId = StringUtils.join(lstCustomerType, ",");
			for (int i = 0; i < lstCustomerType.size(); i++) {
				List<Long> temp = channelTypeMgr.getListDescendant(lstCustomerId, lstCustomerType.get(i));
				removelist.addAll(temp);
			}
			lstCustomerType.removeAll(removelist);
		}
		return lstCustomerType;
	}

	/**
	 * Cap nhat so suat NVBH
	 * 
	 * @author lacnv1
	 * @since Aug 26, 2014
	 */
	public String updateSalerQuantity() throws Exception {
		actionStartTime = new Date();
		resetToken(result);
		result.put(ERROR, true);
		if (promotionId == null || promotionId < 1 || id == null || id < 1) {
			result.put("errMsg", ValidateUtil.getErrorMsg(ConstantManager.ERR_NOT_DATA_CORRECT));
			return JSON;
		}
		try {
			staff = getStaffByCurrentUser();
			if (staff == null || staff.getShop() == null || staff.getStaffType() == null) {
				result.put("errMsg", ValidateUtil.getErrorMsg(ConstantManager.ERR_NOT_PERMISSION, ""));
				return JSON;
			}

			PromotionProgram pro = promotionProgramMgr.getPromotionProgramById(promotionId);
			if (pro == null) {
				result.put("errMsg", ValidateUtil.getErrorMsg(ConstantManager.ERR_NOT_EXIST_DB, "CTKM"));
				return JSON;
			}
			if (!ActiveType.WAITING.equals(pro.getStatus()) && !ActiveType.RUNNING.equals(pro.getStatus())) {
				result.put("errMsg", Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "promotion.program.incorrect"));
				return JSON;
			}

			PromotionStaffMap psm = promotionProgramMgr.getPromotionStaffMapById(id);
			if (psm == null || !ActiveType.RUNNING.equals(psm.getStatus())) {
				result.put("errMsg", Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "catalog.promotion.staff.map.not.exists"));
				return JSON;
			}
			if (quantity != null && psm.getQuantityReceivedTotal() != null && quantity.compareTo(psm.getQuantityReceivedTotal()) < 0) {
				result.put("errMsg", "Số suất phân bổ không được bé hơn số suất đã nhận.");
				return JSON;
			} else if (amount != null && psm.getAmountReceivedTotal() != null && amount.compareTo(psm.getAmountReceivedTotal()) < 0) {
				result.put("errMsg", "Số tiền phân bổ không được bé hơn số tiền đã nhận.");
				return JSON;
			} else if (number != null && psm.getNumReceivedTotal() != null && number.compareTo(psm.getNumReceivedTotal()) < 0) {
				result.put("errMsg", "Số lượng phân bổ không được bé hơn số lượng đã nhận.");
				return JSON;
			} else {
				psm.setQuantityMax(quantity);
				psm.setAmountMax(amount);
				psm.setNumMax(number);
				promotionProgramMgr.updatePromotionStaffMap(psm, getLogInfoVO());
				result.put(ERROR, false);
			}
		} catch (Exception ex) {
			LogUtility.logErrorStandard(ex, R.getResource("web.log.message.error", "vnm.web.action.program.PromotionCatalogAction.updateSalerQuantity"), createLogErrorStandard(actionStartTime));
			result.put("errMsg", ValidateUtil.getErrorMsg(ConstantManager.ERR_SYSTEM));
			result.put(ERROR, true);
		}
		return JSON;
	}

	/**
	 * Xoa so suat NVBH
	 * 
	 * @author lacnv1
	 * @since Aug 19, 2014
	 */
	public String deleteStaffMap() throws Exception {
		actionStartTime = new Date();
		resetToken(result);
		result.put(ERROR, true);
		if (promotionId == null || promotionId < 1) {
			result.put("errMsg", ValidateUtil.getErrorMsg(ConstantManager.ERR_NOT_DATA_CORRECT));
			return JSON;
		}
		try {
			staff = getStaffByCurrentUser();
			if (staff == null || staff.getShop() == null || staff.getStaffType() == null) {
				result.put("errMsg", ValidateUtil.getErrorMsg(ConstantManager.ERR_NOT_PERMISSION, ""));
				return JSON;
			}
			PromotionProgram pro = promotionProgramMgr.getPromotionProgramById(promotionId);
			if (pro == null) {
				result.put("errMsg", ValidateUtil.getErrorMsg(ConstantManager.ERR_NOT_EXIST_DB, "CTKM"));
				return JSON;
			}
			if (pro.getStatus().equals(ActiveType.RUNNING)) {
				result.put("errMsg", "Chương trình đang ở trạng thái hoạt động.");
				return JSON;
			}
			/*
			 * if (!ActiveType.WAITING.equals(pro.getStatus())) {
			 * result.put("errMsg",
			 * Configuration.getResourceString(ConstantManager.VI_LANGUAGE,
			 * "promotion.program.incorrect")); return JSON; }
			 */
			if (id != null && id > 0) {
				PromotionStaffMap psm = promotionProgramMgr.getPromotionStaffMapById(id);
				if (psm == null || !ActiveType.RUNNING.equals(psm.getStatus())) {
					result.put("errMsg", Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "catalog.promotion.staff.map.not.exists"));
					return JSON;
				}
				psm.setStatus(ActiveType.DELETED);
				promotionProgramMgr.updatePromotionStaffMap(psm, getLogInfoVO());
				result.put(ERROR, false);
				return JSON;
			}
			if (shopId == null || shopId <= 0) {
				result.put("errMsg", ValidateUtil.getErrorMsgQuickly(ConstantManager.ERR_NOT_EXIST_DB, false, "catalog.focus.program.shop.code"));
				result.put(ERROR, true);
				return JSON;
			}
			Shop sh = shopMgr.getShopById(shopId);
			if (sh == null) {
				result.put("errMsg", ValidateUtil.getErrorMsgQuickly(ConstantManager.ERR_NOT_EXIST_DB, false, "catalog.focus.program.shop.code"));
				return JSON;
			}
			promotionProgramMgr.removePromotionStaffMap(promotionId, shopId, getLogInfoVO());
			result.put(ERROR, false);
		} catch (Exception ex) {
			LogUtility.logErrorStandard(ex, R.getResource("web.log.message.error", "vnm.web.action.program.PromotionCatalogAction.deleteStaffMap"), createLogErrorStandard(actionStartTime));
			result.put("errMsg", ValidateUtil.getErrorMsg(ConstantManager.ERR_SYSTEM));
			result.put(ERROR, true);
		}
		return JSON;
	}

	private List<TreeGridNode<PromotionStaffVO>> lstStaffTree;

	/**
	 * Tim kiem don vi them vao CTKM
	 * 
	 * @author lacnv1
	 * @since Aug 14, 2014
	 */
	public String searchSalerOnDlg() throws Exception {
		actionStartTime = new Date();
		if (promotionId == null || promotionId <= 0) {
			return JSON;
		}
		try {
			staff = getStaffByCurrentUser();
			if (staff == null || staff.getShop() == null || staff.getStaffType() == null) {
				result.put("errMsg", ValidateUtil.getErrorMsg(ConstantManager.ERR_NOT_PERMISSION, ""));
				return JSON;
			}
			if (id != null) {
				shopId = id;
			}
			PromotionStaffFilter filter = new PromotionStaffFilter();

			Shop sh;
			if (shopId == null || shopId == 0) {
				//sh = staff.getShop();
				//shopId = sh.getId();
				shopId = currentUser.getShopRoot().getShopId();
			}
			sh = shopMgr.getShopById(shopId);
			filter.setPromotionId(promotionId);
			filter.setStrListShopId(getStrListShopId());
			filter.setShopId(shopId);
			if (sh != null && sh.getType().getSpecificType() != null) {
				filter.setShopType(sh.getType().getSpecificType().getValue());
			}
			filter.setCode(code);
			filter.setName(name);
			List<PromotionStaffVO> lst = promotionProgramMgr.searchStaffForPromotion(filter);
			lstStaffTree = new ArrayList<TreeGridNode<PromotionStaffVO>>();
			if (lst == null || lst.size() == 0) {
				return JSON;
			}

			if (id == null) {
				PromotionStaffVO vo;
				int i;
				int sz = lst.size();
				String state;

				if (StringUtil.isNullOrEmpty(code) && StringUtil.isNullOrEmpty(name)) {
					vo = new PromotionStaffVO();
					vo.setId(sh.getId());
					vo.setCode(sh.getShopCode());
					vo.setName(sh.getShopName());
					vo.setIsSaler(0);

					state = ConstantManager.JSTREE_STATE_CLOSE;
					i = 0;
				} else {
					vo = lst.get(0);
					state = ConstantManager.JSTREE_STATE_OPEN;
					i = 1;
				}

				TreeGridNode<PromotionStaffVO> node = new TreeGridNode<PromotionStaffVO>();
				node.setNodeId("sh" + vo.getId());
				node.setAttr(vo);
				node.setState(ConstantManager.JSTREE_STATE_OPEN);
				node.setText(vo.getCode() + " - " + vo.getName());
				List<TreeGridNode<PromotionStaffVO>> chidren = new ArrayList<TreeGridNode<PromotionStaffVO>>();
				node.setChildren(chidren);
				lstStaffTree.add(node);

				// Tao cay			
				TreeGridNode<PromotionStaffVO> tmp;
				TreeGridNode<PromotionStaffVO> tmp2;
				for (; i < sz; i++) {
					vo = lst.get(i);

					tmp2 = getNodeFromTree(lstStaffTree, "sh" + vo.getParentId());
					if (tmp2 != null) {
						tmp = new TreeGridNode<PromotionStaffVO>();
						tmp.setAttr(vo);
						if (0 == vo.getIsSaler()) {
							tmp.setNodeId("sh" + vo.getId());
							tmp.setState(state);
						} else {
							tmp.setNodeId("st" + vo.getId());
							tmp.setState(ConstantManager.JSTREE_STATE_LEAF);
						}
						tmp.setText(vo.getCode() + " - " + vo.getName());

						if (tmp2.getChildren() == null) {
							tmp2.setChildren(new ArrayList<TreeGridNode<PromotionStaffVO>>());
						}
						tmp2.getChildren().add(tmp);
					}
				}
			} else {
				// Tao cay			
				TreeGridNode<PromotionStaffVO> tmp;
				PromotionStaffVO vo;
				for (int i = 0, sz = lst.size(); i < sz; i++) {
					vo = lst.get(i);

					tmp = new TreeGridNode<PromotionStaffVO>();
					tmp.setAttr(vo);
					if (0 == vo.getIsSaler()) {
						tmp.setNodeId("sh" + vo.getId());
						tmp.setState(ConstantManager.JSTREE_STATE_CLOSE);
					} else {
						tmp.setNodeId("st" + vo.getId());
						tmp.setState(ConstantManager.JSTREE_STATE_LEAF);
					}
					tmp.setText(vo.getCode() + " - " + vo.getName());

					lstStaffTree.add(tmp);
				}
			}
		} catch (Exception ex) {
			LogUtility.logErrorStandard(ex, R.getResource("web.log.message.error", "vnm.web.action.program.PromotionCatalogAction.searchSalerOnDlg"), createLogErrorStandard(actionStartTime));
			result.put("errMsg", ValidateUtil.getErrorMsg(ConstantManager.ERR_SYSTEM));
			result.put(ERROR, true);
		}
		return JSON;
	}

	/**
	 * Them NVBH vao CTKM
	 * 
	 * @author lacnv1
	 * @since Aug 27, 2014
	 */
	public String addPromotionStaff() throws Exception {
		actionStartTime = new Date();
		resetToken(result);
		result.put(ERROR, true);
		if (promotionId == null || promotionId < 1 || lstId == null || lstId.size() == 0 || lstQtt == null || lstQtt.size() == 0 || lstId.size() != lstQtt.size()) {
			result.put("errMsg", ValidateUtil.getErrorMsg(ConstantManager.ERR_NOT_DATA_CORRECT));
			return JSON;
		}
		try {
			staff = getStaffByCurrentUser();
			if (staff == null || staff.getShop() == null || staff.getStaffType() == null) {
				result.put("errMsg", ValidateUtil.getErrorMsg(ConstantManager.ERR_NOT_PERMISSION, ""));
				return JSON;
			}
			List<PromotionStaffVO> lstTmp = promotionProgramMgr.getListStaffInPromotion(promotionId, lstId);
			if (lstTmp != null && lstTmp.size() > 0) {
				String msg = "";
				for (PromotionStaffVO vo : lstTmp) {
					msg += (", " + vo.getCode());
				}
				msg = msg.replaceFirst(", ", "");
				result.put("errMsg", Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "catalog.promotion.customer.map.exists", msg));
				return JSON;
			}
			//			lstTmp = null;
			PromotionProgram pro = promotionProgramMgr.getPromotionProgramById(promotionId);
			if (pro == null) {
				result.put("errMsg", ValidateUtil.getErrorMsg(ConstantManager.ERR_NOT_EXIST_DB, "CTKM"));
				return JSON;
			}
			if (!ActiveType.WAITING.equals(pro.getStatus()) && !ActiveType.RUNNING.equals(pro.getStatus())) {
				result.put("errMsg", Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "promotion.program.incorrect"));
				return JSON;
			}
			List<PromotionStaffMap> lst = new ArrayList<PromotionStaffMap>();
			PromotionShopMap psm = null;
			PromotionStaffMap pstm;
			Staff st;
			Date now = DateUtil.now();
			Long shIdT = null;
			for (int i = 0, sz = lstId.size(); i < sz; i++) {
				st = staffMgr.getStaffById(lstId.get(i));
				if (staff == null) {
					result.put("errMsg", ValidateUtil.getErrorMsg(ConstantManager.ERR_NOT_EXIST_DB, "NVBH"));
					return JSON;
				}
				if (!ActiveType.RUNNING.equals(st.getStatus())) {
					result.put("errMsg", ValidateUtil.getErrorMsg(ConstantManager.ERR_STATUS_INACTIVE, st.getStaffCode()));
					return JSON;
				}
				if (!StaffSpecificType.STAFF.getValue().equals(st.getStaffType().getSpecificType().getValue()) && !StaffSpecificType.SUPERVISOR.getValue().equals(st.getStaffType().getSpecificType().getValue())) {
					result.put("errMsg", Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "catalog.not.nvbh", st.getStaffCode()));
					return JSON;
				}
				if (psm == null || !st.getShop().getId().equals(shIdT)) {
					psm = promotionProgramMgr.getPromotionParentShopMap(promotionId, st.getShop().getId(), ActiveType.RUNNING.getValue());
				}
				if (psm == null || !ActiveType.RUNNING.equals(psm.getStatus())) {
					result.put("errMsg", Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "promotion.shop.not.exist"));
					return JSON;
				}
				shIdT = st.getShop().getId();
				List<PromotionStaffMap> listPromotionStaffMap = promotionProgramMgr.getListPromotionStaffMapAddPromotionStaff(psm.getId(), st.getId(), shIdT);
				if (listPromotionStaffMap != null && listPromotionStaffMap.size() > 0) {
					for (PromotionStaffMap staffMap : listPromotionStaffMap) {
						String staffCode = staffMap.getStaff().getStaffCode();
						StringBuilder sb = new StringBuilder("Nhân viên có mã ");
						sb.append(staffCode);
						sb.append(" có ");
						if (lstQtt.get(i) != null && staffMap.getQuantityReceivedTotal() != null && lstQtt.get(i).compareTo(staffMap.getQuantityReceivedTotal()) < 0) {
							sb.append("số suất phân bổ không được bé hơn số suất đã nhận.");
							result.put("errMsg", sb.toString());
							return JSON;
						} else if (lstAmt.get(i) != null && staffMap.getAmountReceivedTotal() != null && lstAmt.get(i).compareTo(staffMap.getAmountReceivedTotal()) < 0) {
							sb.append("số tiền phân bổ không được bé hơn số tiền đã nhận.");
							result.put("errMsg", sb.toString());
							return JSON;
						} else if (lstNum.get(i) != null && staffMap.getNumReceivedTotal() != null && lstNum.get(i).compareTo(staffMap.getNumReceivedTotal()) < 0) {
							sb.append("số lượng phân bổ không được bé hơn số lượng đã nhận.");
							result.put("errMsg", sb.toString());
							return JSON;
						}
						if (lstQtt.get(i) > ZEZO) {
							staffMap.setQuantityMax(lstQtt.get(i));
						}
						if (BigDecimal.ZERO.compareTo(lstAmt.get(i)) < 0) {
							staffMap.setAmountMax(lstAmt.get(i));
						}
						if (BigDecimal.ZERO.compareTo(lstNum.get(i)) < 0) {
							staffMap.setNumMax(lstNum.get(i));
						}
						lst.add(staffMap);
					}
				} else {
					pstm = new PromotionStaffMap();
					pstm.setPromotionShopMap(psm);
					pstm.setShop(st.getShop());
					pstm.setStaff(st);
					if (lstQtt.get(i) > ZEZO) {
						pstm.setQuantityMax(lstQtt.get(i));
					}
					if (BigDecimal.ZERO.compareTo(lstAmt.get(i)) < 0) {
						pstm.setAmountMax(lstAmt.get(i));
					}
					if (BigDecimal.ZERO.compareTo(lstNum.get(i)) < 0) {
						pstm.setNumMax(lstNum.get(i));
					}
					pstm.setStatus(ActiveType.RUNNING);
					pstm.setCreateDate(now);
					pstm.setCreateUser(staff.getStaffCode());

					lst.add(pstm);
				}
			}
			promotionProgramMgr.createListPromotionStaffMap(lst, getLogInfoVO());
			result.put(ERROR, false);
		} catch (Exception ex) {
			LogUtility.logErrorStandard(ex, R.getResource("web.log.message.error", "vnm.web.action.program.PromotionCatalogAction.addPromotionStaff"), createLogErrorStandard(actionStartTime));
			result.put("errMsg", ValidateUtil.getErrorMsg(ConstantManager.ERR_SYSTEM));
			result.put(ERROR, true);
		}
		return JSON;
	}

	/**
	 * Xuat excel ds NVBH thuoc CTKM
	 * 
	 * @author lacnv1
	 * @since Aug 27, 2014
	 */
	public String exportPromotionStaff() throws Exception {
		actionStartTime = new Date();
		if (promotionId == null || promotionId <= 0) {
			result.put(ERROR, false);
			result.put("hasData", false);
			return JSON;
		}
		InputStream inputStream = null;
		try {
			result.put(ERROR, true);
			if (staff == null || staff.getShop() == null || staff.getStaffType() == null) {
				result.put("errMsg", ValidateUtil.getErrorMsg(ConstantManager.ERR_NOT_PERMISSION, ""));
				return JSON;
			}

			String reportToken = retrieveReportToken(reportCode);
			if (vnm.web.utils.StringUtil.isNullOrEmpty(reportToken)) {
				result.put(ERROR, true);
				result.put("errMsg", Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "report.invalid.token"));
				return JSON;
			}

			PromotionShopMapFilter filter = new PromotionShopMapFilter();
			filter.setProgramId(promotionId);
			filter.setStatus(ActiveType.RUNNING);
			filter.setParentShopId(currentUser.getShopRoot().getShopId());
			//			filter.setShopCode(code);
			//			filter.setShopName(name);
			//			filter.setQuantityMax(quantity);
			List<PromotionShopMapVO> lst = promotionProgramMgr.getListPromotionShopMapVO2(filter);
			if (lst == null || lst.size() == 0) {
				result.put(ERROR, false);
				result.put("hasData", false);
				return JSON;
			}
			String folder = ServletActionContext.getServletContext().getRealPath("/") + Configuration.getExcelTemplatePathCatalog();
			StringBuilder sb = new StringBuilder(folder).append(ConstantManager.TEMPLATE_PROMOTION_STAFF_MAP_EXPORT).append(FileExtension.XLS.getValue());
			String templateFileName = sb.toString();
			templateFileName = templateFileName.replace('/', File.separatorChar);

			sb = new StringBuilder(ConstantManager.TEMPLATE_PROMOTION_STAFF_MAP_EXPORT).append(DateUtil.toDateString(DateUtil.now(), DateUtil.DATE_FORMAT_EXCEL_FILE)).append(FileExtension.XLS.getValue());
			String outputName = sb.toString();
			//			sb = null;
			String exportFileName = Configuration.getStoreRealPath() + outputName;

			Map<String, Object> beans = new HashMap<String, Object>();

			beans.put("hasData", 1);
			beans.put("lstShop", lst);

			inputStream = new BufferedInputStream(new FileInputStream(templateFileName));
			XLSTransformer transformer = new XLSTransformer();
			Workbook resultWorkbook = transformer.transformXLS(inputStream, beans);
			inputStream.close();
			OutputStream os = new BufferedOutputStream(new FileOutputStream(exportFileName));
			resultWorkbook.write(os);
			os.flush();
			os.close();
			result.put(ERROR, false);
			String outputPath = Configuration.getExportExcelPath() + outputName;
			result.put(REPORT_PATH, outputPath);
			MemcachedUtils.putValueToMemcached(reportToken, outputPath, retrieveReportMemcachedTimeout());
		} catch (Exception ex) {
			LogUtility.logErrorStandard(ex, R.getResource("web.log.message.error", "vnm.web.action.program.PromotionCatalogAction.exportPromotionStaff"), createLogErrorStandard(actionStartTime));
			result.put("errMsg", ValidateUtil.getErrorMsg(ConstantManager.ERR_SYSTEM));
			result.put(ERROR, true);
		} finally{
			if ( inputStream != null  ) { 
				try { 
					inputStream.close();	  
				} 
				catch (Exception ignore) {
					LogUtility.logError(ignore, ignore.getMessage());
				} 
			}
		}
		return JSON;
	}

	/**
	 * Xuat excel ds NVBH thuoc CTKM
	 * 
	 * @author lacnv1
	 * @since Aug 27, 2014
	 */
	public String exportPromotionCustomer() throws Exception {
		actionStartTime = new Date();
		if (promotionId == null || promotionId <= 0) {
			result.put(ERROR, false);
			result.put("hasData", false);
			return JSON;
		}
		InputStream inputStream = null;
		try {
			result.put(ERROR, true);
			if (staff == null || staff.getShop() == null || staff.getStaffType() == null) {
				result.put("errMsg", ValidateUtil.getErrorMsg(ConstantManager.ERR_NOT_PERMISSION, ""));
				return JSON;
			}

			String reportToken = retrieveReportToken(reportCode);
			if (vnm.web.utils.StringUtil.isNullOrEmpty(reportToken)) {
				result.put(ERROR, true);
				result.put("errMsg", Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "report.invalid.token"));
				return JSON;
			}
			PromotionShopMapFilter filter = new PromotionShopMapFilter();
			filter.setPromotionId(promotionId);
			filter.setAddress(address);
			filter.setProgramId(promotionId);
			filter.setStatus(ActiveType.RUNNING);
			filter.setParentShopId(currentUser.getShopRoot().getShopId());
			filter.setCusCode(code);
			filter.setCusName(name);
			List<PromotionShopMapVO> lst = promotionProgramMgr.getListPromotionShopMapVO3(filter);
			if (lst == null || lst.size() == 0) {
				result.put(ERROR, false);
				result.put("hasData", false);
				return JSON;
			}
			String folder = ServletActionContext.getServletContext().getRealPath("/") + Configuration.getExcelTemplatePathCatalog();
			StringBuilder sb = new StringBuilder(folder).append(ConstantManager.TEMPLATE_PROMOTION_CUSTOMER_MAP_EXPORT).append(FileExtension.XLS.getValue());
			String templateFileName = sb.toString();
			templateFileName = templateFileName.replace('/', File.separatorChar);

			sb = new StringBuilder(ConstantManager.TEMPLATE_PROMOTION_CUSTOMER_MAP_EXPORT).append(DateUtil.toDateString(DateUtil.now(), DateUtil.DATE_FORMAT_EXCEL_FILE)).append(FileExtension.XLS.getValue());
			String outputName = sb.toString();
			//			sb = null;
			String exportFileName = Configuration.getStoreRealPath() + outputName;

			Map<String, Object> beans = new HashMap<String, Object>();

			beans.put("hasData", 1);
			beans.put("lstShop", lst);

			inputStream = new BufferedInputStream(new FileInputStream(templateFileName));
			XLSTransformer transformer = new XLSTransformer();
			Workbook resultWorkbook = transformer.transformXLS(inputStream, beans);
			inputStream.close();
			OutputStream os = new BufferedOutputStream(new FileOutputStream(exportFileName));
			resultWorkbook.write(os);
			os.flush();
			os.close();
			result.put(ERROR, false);
			String outputPath = Configuration.getExportExcelPath() + outputName;
			result.put(REPORT_PATH, outputPath);
			MemcachedUtils.putValueToMemcached(reportToken, outputPath, retrieveReportMemcachedTimeout());
		} catch (Exception ex) {
			LogUtility.logErrorStandard(ex, R.getResource("web.log.message.error", "vnm.web.action.program.PromotionCatalogAction.exportPromotionCustomer"), createLogErrorStandard(actionStartTime));
			result.put("errMsg", ValidateUtil.getErrorMsg(ConstantManager.ERR_SYSTEM));
			result.put(ERROR, true);
		} finally{
			if ( inputStream != null  ) { 
				try { 
					inputStream.close();	  
				} 
				catch (Exception ignore) {
					LogUtility.logError(ignore, ignore.getMessage());
				} 
			}
		}
		return JSON;
	}

	/**
	 * Nhap so suat NVBH tu excel
	 * 
	 * @author lacnv1
	 * @since Aug 27, 2014
	 * 
	 * @modify hunglm16
	 * @sice 14/09/2015
	 * @description Bo sung them cot So tien va So luong
	 */
	public String importPromotionStaff() throws Exception {
		actionStartTime = new Date();
		resetToken(result);
		isError = true;
		try {
			PromotionProgram promotionProgram = promotionProgramMgr.getPromotionProgramById(promotionId);
			if (promotionProgram == null) {
				isError = true;
				errMsg = ValidateUtil.getErrorMsg(ConstantManager.ERR_NOT_EXIST_DB, "CTKM");
				return SUCCESS;
			}
			totalItem = 0;
			String message;
			String msg;
			lstView = new ArrayList<CellBean>();
			typeView = true;
			List<CellBean> lstFails = new ArrayList<CellBean>();
			List<List<String>> lstData = getExcelDataEx(excelFile, excelFileContentType, errMsg, 5);
			if (StringUtil.isNullOrEmpty(errMsg) && lstData != null && lstData.size() > 0) {
				PromotionStaffMap promotionStaffMap;
				List<String> row;
				Shop shop = null;
				boolean isCreateStaff;
				PromotionShopMap psm = null;
				Staff st = null;
				LogInfoVO logInfo = getLogInfoVO();
				String value;

				for (int i = 0; i < lstData.size(); i++) {
					if (lstData.get(i) != null && lstData.get(i).size() > 0) {
						boolean flagContinue = true;
						//Bo qua cac dong trong
						for (int j = 0; j < 5; j++) {
							if (!StringUtil.isNullOrEmpty(lstData.get(i).get(j))) {
								flagContinue = false;
							}
						}
						if (flagContinue) {
							continue;
						}
						message = "";
						//						msg = "";
						totalItem++;
						row = lstData.get(i);
						isCreateStaff = false;
						promotionStaffMap = new PromotionStaffMap();
						// Ma don vi
						value = row.get(0);
						if (!StringUtil.isNullOrEmpty(value)) {
							shop = shopMgr.getShopByCode(value);
							if (shop == null) {
								message += ValidateUtil.getErrorMsgQuickly(ConstantManager.ERR_NOT_EXIST_DB, true, "catalog.focus.program.shop.code");
							} else if (!ActiveType.RUNNING.equals(shop.getStatus())) {
								message += ValidateUtil.getErrorMsgQuickly(ConstantManager.ERR_STATUS_INACTIVE, true, "catalog.focus.program.shop.code");
							} else if (!checkShopPermission(shop.getShopCode())) {
								message += ValidateUtil.getErrorMsg(ConstantManager.ERR_INVALID_SHOP, shop.getShopCode(), currentUser.getUserName()) + "\n";
							}
						} else {
							message += R.getResource("common.missing.not.in.system.p", R.getResource("catalog.focus.program.shop.code")) + "\n";
						}
						if (shop != null) {
							psm = promotionProgramMgr.getPromotionShopMap(shop.getId(), promotionProgram.getId());
						}
						if (StringUtil.isNullOrEmpty(message)) {
							if (psm != null) {
								if (!ActiveType.RUNNING.equals(psm.getStatus())) {
									message += Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "promotion.shop.not.exist") + "\n";
								}
							} else {
								message += Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "promotion.shop.not.exist") + "\n";
							}
						}
						// NVBH
						value = row.get(1);
						if (!StringUtil.isNullOrEmpty(value) && shop != null) {
							st = staffMgr.getStaffByCode(value);
							if (st == null) {
								message += ValidateUtil.getErrorMsg(ConstantManager.ERR_NOT_EXIST_DB, "NV " + value) + "\n";
							} else {
								if (!ActiveType.RUNNING.equals(st.getStatus())) {
									message += ValidateUtil.getErrorMsg(ConstantManager.ERR_STATUS_INACTIVE, "NV") + "\n";
								} else if (st.getStaffType() == null || st.getStaffType().getSpecificType() == null || !StaffSpecificType.STAFF.getValue().equals(st.getStaffType().getSpecificType().getValue())) {
									//&& !StaffSpecificType.SUPERVISOR.getValue().equals(st.getStaffType().getSpecificType().getValue())
									message += Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "catalog.not.nvbh", st.getStaffCode()) + "\n";
								} else if (st.getShop() != null && !st.getShop().getId().equals(shop.getId())) {
									message += Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "customerdebit.batch.import.not.in.shop", "NV") + "\n";
								}
								if (psm != null && st != null) {
									promotionStaffMap = promotionProgramMgr.getPromotionStaffMapByShopMapAndStaff(psm.getId(), st.getId());
									if (promotionStaffMap == null) {
										isCreateStaff = true;
										promotionStaffMap = new PromotionStaffMap();
									}
								}
								if (promotionStaffMap != null) {
									promotionStaffMap.setPromotionShopMap(psm);
									promotionStaffMap.setStaff(st);
								}
							}
						} else {
							message += R.getResource("common.missing.not.in.system.p", R.getResource("ss.traningplan.salestaff.code")) + "\n";
						}
						// So suat NVBH
						value = row.get(2);
						msg = "";
						if (!StringUtil.isNullOrEmpty(value)) {
							if (value.trim().length() > maxlengthNumber || !StringUtil.isNumberInt(value)) {
								msg = R.getResource("catalog.promotion.import.khtg.ctkm.value.number.integer", R.getResource("catalog.suatKH.code"));
							} else {
								if (Integer.valueOf(value) > 0) {
									promotionStaffMap.setQuantityMax(Integer.valueOf(value));
								} else {
									msg = Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "common.not.negative", Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "catalog.suatNPP.code"));
								}
							}
						} else if (promotionStaffMap != null) {
							promotionStaffMap.setQuantityMax(null);
						}
						if (!StringUtil.isNullOrEmpty(msg)) {
							message += msg + "\n";
						}

						//So tien
						value = row.get(3);
						msg = "";
						if (!StringUtil.isNullOrEmpty(value)) {
							if (value.trim().length() > maxlengthNumber || !StringUtil.isNumberInt(value)) {
								msg = R.getResource("catalog.promotion.import.khtg.ctkm.value.number.integer", R.getResource("catalog.promotion.import.khtg.ctkm.clmn.amount"));
							} else {
								if (Integer.valueOf(value) > 0) {
									promotionStaffMap.setAmountMax(BigDecimal.valueOf(Long.valueOf(value)));
								} else {
									msg = Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "common.not.negative", Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "catalog.promotion.import.khtg.ctkm.clmn.amount"));
								}
							}
						} else if (promotionStaffMap != null) {
							promotionStaffMap.setAmountMax(null);
						}
						if (!StringUtil.isNullOrEmpty(msg)) {
							message += msg + "\n";
						}
						//So luong
						value = row.get(4);
						msg = "";
						if (!StringUtil.isNullOrEmpty(value)) {
							if (value.trim().length() > maxlengthNumber || !StringUtil.isNumberInt(value)) {
								msg = R.getResource("catalog.promotion.import.khtg.ctkm.value.number.integer", R.getResource("catalog.promotion.import.khtg.ctkm.clmn.quantity"));
							} else {
								if (Integer.valueOf(value) > 0) {
									promotionStaffMap.setNumMax(BigDecimal.valueOf(Long.valueOf(value)));
								} else {
									msg = Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "common.not.negative", Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "catalog.promotion.import.khtg.ctkm.clmn.quantity"));
								}
							}
						} else if (promotionStaffMap != null) {
							promotionStaffMap.setNumMax(null);
						}
						if (!StringUtil.isNullOrEmpty(msg)) {
							message += msg + "\n";
						}
						//Kiem tra dieu kien thoa man va cap nhat xuong DB
						if (StringUtil.isNullOrEmpty(message)) {
							try {
								if (promotionStaffMap != null && promotionStaffMap.getStaff() != null) {
									promotionStaffMap.setShop(st != null ? st.getShop() : null);
									if (isCreateStaff) {
										promotionProgramMgr.createPromotionStaffMap(promotionStaffMap, logInfo);
									} else {
										promotionProgramMgr.updatePromotionStaffMap(promotionStaffMap, logInfo);
									}
								}
								isError = false;
							} catch (BusinessException be) {
								LogUtility.logErrorStandard(be, R.getResource("web.log.message.error", "vnm.web.action.program.PromotionCatalogAction.importPromotionStaff"), createLogErrorStandard(actionStartTime));
								errMsg = ValidateUtil.getErrorMsg(ConstantManager.ERR_SYSTEM);
							}
						} else {
							lstFails.add(StringUtil.addFailBean(row, message));
						}
						typeView = false;
					}
				}
				getOutputFailExcelFile(lstFails, ConstantManager.TEMPLATE_CATALOG_PP_STAFF_FAIL);
			}
			if (StringUtil.isNullOrEmpty(errMsg)) {
				isError = false;
			}
		} catch (Exception ex) {
			LogUtility.logErrorStandard(ex, R.getResource("web.log.message.error", "vnm.web.action.program.PromotionCatalogAction.importPromotionStaff"), createLogErrorStandard(actionStartTime));
			errMsg = ValidateUtil.getErrorMsg(ConstantManager.ERR_SYSTEM);
			isError = true;
		}
		return SUCCESS;
	}

	/**
	 * Nhap so suat NVBH tu excel
	 * 
	 * @author lacnv1
	 * @since Aug 27, 2014
	 * 
	 * @modify hunglm16
	 * @sice 14/09/2015
	 * @description Bo sung them cot So tien va So luong
	 */
	public String importPromotionCustomer() throws Exception {
		actionStartTime = new Date();
		resetToken(result);
		isError = true;
		try {
			PromotionProgram promotionProgram = promotionProgramMgr.getPromotionProgramById(promotionId);
			if (promotionProgram == null) {
				isError = true;
				errMsg = ValidateUtil.getErrorMsg(ConstantManager.ERR_NOT_EXIST_DB, "CTKM");
				return SUCCESS;
			}
			totalItem = 0;
			String message;
			String msg;
			lstView = new ArrayList<CellBean>();
			typeView = true;
			List<CellBean> lstFails = new ArrayList<CellBean>();
			List<List<String>> lstData = getExcelDataEx(excelFile, excelFileContentType, errMsg, 5);
			if (StringUtil.isNullOrEmpty(errMsg) && lstData != null && lstData.size() > 0) {
				PromotionCustomerMap promotionCustomerMap;
				List<String> row;
				Shop shop = null;
				boolean isCreateCustomer;
				PromotionShopMap psm = null;
				Customer cust = null;
				LogInfoVO logInfo = getLogInfoVO();
				String value;

				for (int i = 0, sizei = lstData.size(); i < sizei; i++) {
					if (lstData.get(i) != null && lstData.get(i).size() > 0) {
						boolean flagContinue = true;
						//Bo qua cac dong trong
						for (int j = 0; j < 5; j++) {
							if (!StringUtil.isNullOrEmpty(lstData.get(i).get(j))) {
								flagContinue = false;
							}
						}
						if (flagContinue) {
							continue;
						}
						message = "";
						totalItem++;
						row = lstData.get(i);
						isCreateCustomer = false;
						promotionCustomerMap = new PromotionCustomerMap();
						// Ma don vi
						value = row.get(0);
						msg = "";
						if (!StringUtil.isNullOrEmpty(value)) {
							if (!StringUtil.isNullOrEmpty(value)) {
								shop = shopMgr.getShopByCode(value);
								if (shop == null) {
									msg += ValidateUtil.getErrorMsgQuickly(ConstantManager.ERR_NOT_EXIST_DB, true, "catalog.focus.program.shop.code");
								} else if (!ActiveType.RUNNING.equals(shop.getStatus())) {
									msg += ValidateUtil.getErrorMsgQuickly(ConstantManager.ERR_STATUS_INACTIVE, true, "catalog.focus.program.shop.code");
								} else if (!checkShopPermission(shop.getShopCode())) {
									msg += ValidateUtil.getErrorMsg(ConstantManager.ERR_INVALID_SHOP, shop.getShopCode(), currentUser.getUserName()) + "\n";
								}
							} else {
								shop = getCurrentShop();
							}
						} else {
							message += R.getResource("common.missing.not.in.system.p", R.getResource("catalog.focus.program.shop.code")) + "\n";
						}
						if (!StringUtil.isNullOrEmpty(msg)) {
							message += msg + "\n";
						}
						if (shop != null) {
							psm = promotionProgramMgr.getPromotionShopMap(shop.getId(), promotionProgram.getId());
						}
						if (StringUtil.isNullOrEmpty(message)) {
							if (psm != null) {
								if (!ActiveType.RUNNING.equals(psm.getStatus())) {
									message += Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "promotion.shop.not.exist") + "\n";
								}
							} else {
								message += Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "promotion.shop.not.exist") + "\n";
							}
						}

						// KH
						value = row.get(1);
						if (!StringUtil.isNullOrEmpty(value) && shop != null) {
							cust = customerMgr.getCustomerByCode(StringUtil.getFullCode(shop.getShopCode(), value));
							if (cust == null) {
								message += ValidateUtil.getErrorMsg(ConstantManager.ERR_NOT_EXIST_DB, "KH " + value) + "\n";
							} else {
								if (!ActiveType.RUNNING.equals(cust.getStatus())) {
									message += ValidateUtil.getErrorMsg(ConstantManager.ERR_STATUS_INACTIVE, "KH") + "\n";
								}
							}
							if (psm != null && cust != null) {
								promotionCustomerMap = promotionProgramMgr.getPromotionCustomerMap(psm.getId(), cust.getId());
								if (promotionCustomerMap == null) {
									isCreateCustomer = true;
									promotionCustomerMap = new PromotionCustomerMap();
								}
							}
							if (promotionCustomerMap != null) {
								promotionCustomerMap.setPromotionShopMap(psm);
								promotionCustomerMap.setCustomer(cust);
							}
						} else {
							message += R.getResource("common.missing.not.in.system.p", R.getResource("ss.traningplan.salestaff.code")) + "\n";
						}
						// So suat NVBH
						value = row.get(2);
						msg = "";
						if (!StringUtil.isNullOrEmpty(value)) {
							if (value.trim().length() > maxlengthNumber || !StringUtil.isNumberInt(value)) {
								msg = R.getResource("catalog.promotion.import.khtg.ctkm.value.number.integer", R.getResource("catalog.suatKH.code"));
							} else {
								if (Integer.valueOf(value) > 0) {
									if (shop != null) {
										if (psm != null && cust != null) {
											if (promotionCustomerMap != null && promotionCustomerMap.getQuantityReceived() != null && promotionCustomerMap.getQuantityReceived() > Integer.valueOf(value)) {
												msg = Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "catalog.promotion.program.quantityR.less.than.quantityM") + "\n";
											}
										}
										if (StringUtil.isNullOrEmpty(message)) {
											promotionCustomerMap.setQuantityMax(Integer.valueOf(value));
										}
									}
								} else {
									msg = Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "common.not.negative", Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "catalog.suatNPP.code"));
								}
							}
						} else if (promotionCustomerMap != null) {
							promotionCustomerMap.setQuantityMax(null);
						}
						if (!StringUtil.isNullOrEmpty(msg)) {
							message += msg + "\n";
						}

						//So tien
						value = row.get(3);
						msg = "";
						if (!StringUtil.isNullOrEmpty(value)) {
							if (value.trim().length() > maxlengthNumber || !StringUtil.isNumberInt(value)) {
								msg = R.getResource("catalog.promotion.import.khtg.ctkm.value.number.integer", R.getResource("catalog.promotion.import.khtg.ctkm.clmn.amount"));
							} else {
								if (Long.valueOf(value) > 0) {
									promotionCustomerMap.setAmountMax(BigDecimal.valueOf(Long.valueOf(value)));
								} else {
									msg = Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "common.not.negative", Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "catalog.promotion.import.khtg.ctkm.clmn.amount"));
								}
							}
						} else if (promotionCustomerMap != null) {
							promotionCustomerMap.setAmountMax(null);
						}
						if (!StringUtil.isNullOrEmpty(msg)) {
							message += msg + "\n";
						}

						//So luong
						value = row.get(4);
						msg = "";
						if (!StringUtil.isNullOrEmpty(value)) {
							if (value.trim().length() > maxlengthNumber || !StringUtil.isNumberInt(value)) {
								msg = R.getResource("catalog.promotion.import.khtg.ctkm.value.number.integer", R.getResource("catalog.promotion.import.khtg.ctkm.clmn.quantity"));
							} else {
								if (Long.valueOf(value) > 0) {
									promotionCustomerMap.setNumMax(BigDecimal.valueOf(Long.valueOf(value)));
								} else {
									msg = Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "common.not.negative", Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "catalog.promotion.import.khtg.ctkm.clmn.quantity"));
								}
							}
						} else if (promotionCustomerMap != null) {
							promotionCustomerMap.setNumMax(null);
						}
						if (!StringUtil.isNullOrEmpty(msg)) {
							message += msg + "\n";
						}

						//Kiem tra tinh hop le va cap DB
						if (StringUtil.isNullOrEmpty(message)) {
							try {
								if (promotionCustomerMap != null && promotionCustomerMap.getCustomer() != null) {
									promotionCustomerMap.setShop(cust != null ? cust.getShop() : null);
									if (isCreateCustomer) {
										promotionProgramMgr.createPromotionCustomerMap(promotionCustomerMap, logInfo);
									} else {
										promotionProgramMgr.updatePromotionCustomerMap(promotionCustomerMap, logInfo);
									}
								}
								isError = false;
							} catch (BusinessException be) {
								LogUtility.logErrorStandard(be, R.getResource("web.log.message.error", "vnm.web.action.program.PromotionCatalogAction.importPromotionCustomer"), createLogErrorStandard(actionStartTime));
								errMsg = ValidateUtil.getErrorMsg(ConstantManager.ERR_SYSTEM);
							}
						} else {
							lstFails.add(StringUtil.addFailBean(row, message));
						}
						typeView = false;
					}
				}
				getOutputFailExcelFile(lstFails, ConstantManager.TEMPLATE_CATALOG_PP_STAFF_FAIL);
			}
			if (StringUtil.isNullOrEmpty(errMsg)) {
				isError = false;
			}
		} catch (Exception ex) {
			LogUtility.logErrorStandard(ex, R.getResource("web.log.message.error", "vnm.web.action.program.PromotionCatalogAction.importPromotionCustomer"), createLogErrorStandard(actionStartTime));
			Throwable e = (Throwable) ex.getCause();
			String errCode = e.getMessage();
			if (errCode.contains("ORA-20024")) {
				errMsg = Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "promotion.program.not.meet.conditions.new.cus");
			} else {
				errMsg = ValidateUtil.getErrorMsg(ConstantManager.ERR_SYSTEM);
			}
			isError = true;
		}
		return SUCCESS;
	}

	public String deleteLevelDetail() {
		actionStartTime = new Date();
		resetToken(result);
		try {
			promotionProgramMgr.deleteLevelDetail(levelDetailId, getLogInfoVO());
		} catch (Exception e) {
			LogUtility.logErrorStandard(e, R.getResource("web.log.message.error", "vnm.web.action.program.PromotionCatalogAction.deleteLevelDetail"), createLogErrorStandard(actionStartTime));
			result.put("errMsg", ValidateUtil.getErrorMsg(ConstantManager.ERR_SYSTEM));
			result.put(ERROR, true);
		}
		return SUCCESS;
	}

	/**
	 * load page
	 * 
	 * @return
	 */
	public String productConvertDetail() {
		actionStartTime = new Date();
		try {
			promotionProgram = promotionProgramMgr.getPromotionProgramById(id);
			listProduct = promotionProgramMgr.getListProductInSaleLevel(id);
		} catch (Exception e) {
			LogUtility.logErrorStandard(e, R.getResource("web.log.message.error", "vnm.web.action.program.PromotionCatalogAction.productConvertDetail"), createLogErrorStandard(actionStartTime));
			result.put("errMsg", ValidateUtil.getErrorMsg(ConstantManager.ERR_SYSTEM));
			result.put(ERROR, true);
			listProduct = new ArrayList<Product>();
		}
		return SUCCESS;
	}

	/**
	 * load list
	 * 
	 * @author phut
	 * @return
	 */
	public String productConvertLoadGroup() {
		actionStartTime = new Date();
		try {
			listConvertGroup = promotionProgramMgr.listPromotionProductConvertVO(promotionId, null);
		} catch (Exception e) {
			LogUtility.logErrorStandard(e, R.getResource("web.log.message.error", "vnm.web.action.program.PromotionCatalogAction.productConvertLoadGroup"), createLogErrorStandard(actionStartTime));
			result.put("errMsg", ValidateUtil.getErrorMsg(ConstantManager.ERR_SYSTEM));
			result.put(ERROR, true);
			listConvertGroup = new ArrayList<PPConvertVO>();
		}
		return SUCCESS;
	}

	/**
	 * save list ctkm sp quy doi
	 * 
	 * @author phut
	 * @return
	 */
	public String saveProductConvert() {
		actionStartTime = new Date();
		resetToken(result);
		try {
			if (listConvertGroup != null && listConvertGroup.size() > 0) {
				for (PPConvertVO vo : listConvertGroup) {
					if (null == vo.getId()) {
						List<PPConvertVO> __result = promotionProgramMgr.listPromotionProductConvertVO(promotionId, vo.getName());
						if (__result.size() > 0) {
							result.put(ERROR, true);
							result.put("errMsg", Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "promotion.product.convert.duplicate.group.error", vo.getName()));
							return SUCCESS;
						}
					} else {
						/** @author update nhutnn, @since 14/05/2015 */
						if (vo.getListDetail() != null) {
							for (int i = 0, sz = vo.getListDetail().size(); i < sz; i++) {
								if (vo.getListDetail().get(i) != null) {
									String proCode = vo.getListDetail().get(i).getProductCode();
									if (!StringUtil.isNullOrEmpty(proCode)) {
										Product product = productMgr.getProductByCode(proCode);
										if (product == null) {
											result.put(ERROR, true);
											result.put("errMsg", R.getResource("catalog.display.program.update.sale.product.product.not.exists", proCode));
											return SUCCESS;
										}
									} else {
										result.put(ERROR, true);
										result.put("errMsg", R.getResource("price.manage.err.imp.product.isnull"));
										return SUCCESS;
									}
								}
							}
						}
					}
				}
				promotionProgramMgr.savePPConvert(promotionId, listConvertGroup, getLogInfoVO());
			}
		} catch (Exception e) {
			LogUtility.logErrorStandard(e, R.getResource("web.log.message.error", "vnm.web.action.program.PromotionCatalogAction.saveProductConvert"), createLogErrorStandard(actionStartTime));
			result.put("errMsg", ValidateUtil.getErrorMsg(ConstantManager.ERR_SYSTEM));
			result.put(ERROR, true);
		}
		return SUCCESS;
	}

	/**
	 * delete 1 dong CTKM sp quy doi
	 * 
	 * @author phut
	 * @return
	 */
	public String deleteProductConvert() {
		actionStartTime = new Date();
		resetToken(result);
		try {
			promotionProgramMgr.deletePPConvert(id, getLogInfoVO());
		} catch (Exception e) {
			LogUtility.logErrorStandard(e, R.getResource("web.log.message.error", "vnm.web.action.program.PromotionCatalogAction.deleteProductConvert"), createLogErrorStandard(actionStartTime));
			result.put("errMsg", ValidateUtil.getErrorMsg(ConstantManager.ERR_SYSTEM));
			result.put(ERROR, true);
		}
		return SUCCESS;
	}

	/**
	 * delete 1 sp cua 1 dong CTKM sp quy doi
	 * 
	 * @author phut
	 * @return
	 */
	public String deleteProductConvertDetail() {
		actionStartTime = new Date();
		resetToken(result);
		try {
			promotionProgramMgr.deletePPConvertDetail(id, getLogInfoVO());
		} catch (Exception e) {
			LogUtility.logErrorStandard(e, R.getResource("web.log.message.error", "vnm.web.action.program.PromotionCatalogAction.deleteProductConvertDetail"), createLogErrorStandard(actionStartTime));
			result.put("errMsg", ValidateUtil.getErrorMsg(ConstantManager.ERR_SYSTEM));
			result.put(ERROR, true);
		}
		return SUCCESS;
	}

	/**
	 * load page
	 * 
	 * @author phut
	 * @return
	 */
	public String productOpenNewDetail() throws Exception {
		promotionProgram = promotionProgramMgr.getPromotionProgramById(id);
		return SUCCESS;
	}

	/**
	 * load list
	 * 
	 * @author phut
	 * @return
	 */
	public String productOpenNewLoadProduct() {
		actionStartTime = new Date();
		try {
			listProductOpen = promotionProgramMgr.listPromotionProductOpenVO(promotionId);
		} catch (Exception e) {
			listProductOpen = new ArrayList<PromotionProductOpenVO>();
			LogUtility.logErrorStandard(e, R.getResource("web.log.message.error", "vnm.web.action.program.PromotionCatalogAction.productOpenNewLoadProduct"), createLogErrorStandard(actionStartTime));
			result.put("errMsg", ValidateUtil.getErrorMsg(ConstantManager.ERR_SYSTEM));
			result.put(ERROR, true);
		}
		return SUCCESS;
	}

	/**
	 * new/update list ctkm KH mo moi
	 * 
	 * @author phut
	 * @return
	 */
	public String productOpenNewSave() {
		actionStartTime = new Date();
		try {
			promotionProgramMgr.saveListPromotionProductOpen(promotionId, listProductOpen, getLogInfoVO());
		} catch (Exception e) {
			LogUtility.logErrorStandard(e, R.getResource("web.log.message.error", "vnm.web.action.program.PromotionCatalogAction.productOpenNewSave"), createLogErrorStandard(actionStartTime));
			result.put("errMsg", ValidateUtil.getErrorMsg(ConstantManager.ERR_SYSTEM));
			result.put(ERROR, true);
		}
		return SUCCESS;
	}

	/**
	 * delete 1 dong ctkm KH mo moi
	 * 
	 * @author phut
	 * @return
	 */
	public String productOpenNewDelete() {
		actionStartTime = new Date();
		resetToken(result);
		try {
			promotionProgramMgr.deletePromotionProductOpen(id);
		} catch (Exception e) {
			LogUtility.logErrorStandard(e, R.getResource("web.log.message.error", "vnm.web.action.program.PromotionCatalogAction.productOpenNewDelete"), createLogErrorStandard(actionStartTime));
			result.put("errMsg", ValidateUtil.getErrorMsg(ConstantManager.ERR_SYSTEM));
			result.put(ERROR, true);
		}
		return SUCCESS;
	}

	public String getReportNameFormat(String name, String fDate, String tDate, String fileExtension) {
		String nameRpt = "";
		if (fDate.isEmpty() && tDate.isEmpty()) {
			nameRpt = nameRpt + name;
		} else {
			Date fD = DateUtil.parse(fDate, DateUtil.DATE_FORMAT_DDMMYYYY);
			Date tD = DateUtil.parse(tDate, DateUtil.DATE_FORMAT_DDMMYYYY);
			fDate = DateUtil.toDateString(fD, DateUtil.DATE_FORMAT_CSV);
			tDate = DateUtil.toDateString(tD, DateUtil.DATE_FORMAT_CSV);
			nameRpt = nameRpt + name + "_" + fDate + "-" + tDate;
		}
		Random rand = new Random();
		Integer n = rand.nextInt(100) + 1;
		return nameRpt + "_" + n.toString() + fileExtension;
	}

	/**
	 * Xuat danh sach CTKM
	 * 
	 * @author tungmt
	 * @since 27/02/14
	 * @description Xuất XLSX
	 */
	public String export() throws Exception {
		actionStartTime = new Date();
		FileOutputStream out = null;
		SXSSFWorkbook workbook = null;
		try {
			String reportToken = retrieveReportToken(reportCode);
			if (vnm.web.utils.StringUtil.isNullOrEmpty(reportToken)) {
				result.put(ERROR, true);
				result.put("errMsg", Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "report.invalid.token"));
				return JSON;
			}

			result.put(ERROR, true);
			staff = getStaffByCurrentUser();
			if (staff == null || staff.getShop() == null || staff.getStaffType() == null) {
				return JSON;
			}

			PromotionProgramFilter filter = new PromotionProgramFilter();
			Date fDate = null;
			Date tDate = null;
			if (!StringUtil.isNullOrEmpty(fromDate)) {
				fDate = vnm.web.utils.DateUtil.parse(fromDate, DateUtil.DATE_FORMAT_DDMMYYYY);
				filter.setFromDate(fDate);
			}
			if (!StringUtil.isNullOrEmpty(toDate)) {
				tDate = vnm.web.utils.DateUtil.parse(toDate, DateUtil.DATE_FORMAT_DDMMYYYY);
				filter.setToDate(tDate);
			}
			List<String> temp = new ArrayList<String>();
			if (!StringUtil.isNullOrEmpty(lstTypeId)) {
				if (lstTypeId.indexOf("-1") > -1) {
					temp = null;
				} else {
					String[] lstTmp = lstTypeId.split(",");
					if (lstTmp.length > 0) {
						Integer size = lstTmp.length;
						for (int i = 0; i < size; i++) {
							ApParam apParam = apParamMgr.getApParamById(Long.valueOf(lstTmp[i].trim()));
							if (apParam != null) {
								temp.add(apParam.getApParamCode());
							}
						}
					}
				}
			}
			filter.setLstType(temp);

			if (status == null) {
				status = ActiveType.RUNNING.getValue();
			}
			if (!ALL_INTEGER_G.equals(status)) {
				ActiveType at = ActiveType.parseValue(status);
				/**
				 * @author kieupp Them dieu kien tim kiem dang het han chuong
				 *         trinh khuyen mai voi Chuong trinh KM hoat dong nhung
				 *         to_date nho hon ngay hien tai dat bien flag den biet
				 *         dang tim kiem dang o o dang het han
				 */
				if (ActiveType.HET_HAN.equals(at)) {
					tDate = DateUtil.getYesterday(DateUtil.now());
					filter.setToDate(tDate);
					filter.setFlagExpire(true);
					at = ActiveType.RUNNING;
					filter.setStatus(at);
				} else {
					filter.setStatus(at);
				}
			}

			if (StringUtil.isNullOrEmpty(shopCode)) {
				filter.setShopCode(currentUser.getShopRoot().getShopCode());
				filter.setCreateUser(currentUser.getStaffRoot().getStaffCode());
				if (isVNMAdmin) {
					filter.setIsVNM(true);
				}
			} else {
				filter.setShopCode(shopCode);
			}
			filter.setPpCode(code);
			filter.setPpName(name);
			if (!StringUtil.isNullOrEmpty(numberNotify)) {
				filter.setNumberNotify(numberNotify);
			}
			filter.setIsAutoPromotion(ConstantManager.PROMOTION_AUTO == proType);
			filter.setStrListShopId(getStrListShopId());
			ObjectVO<PromotionProgram> objVO = promotionProgramMgr.getListPromotionProgram(filter);
			List<PromotionProgram> lst = objVO != null ? objVO.getLstObject() : null;
			/** Lay va kiem tra co du lieu */
			//			List<RptDM1_1TTKH> lst = hoReportMgr.getDM1_1TTKH(lstShopId, strStatus);
			if (lst == null || lst.size() == 0) {
				result.put(ERROR, false);
				result.put("hasData", false);
				return JSON;
			}

			String fileName = R.getResource("promotion.program.export.name");
			fileName = getReportNameFormat(fileName, "", "", FileExtension.XLSX.getValue());
			//Init XSSF workboook
			workbook = new SXSSFWorkbook(200);
			workbook.setCompressTempFiles(true);
			//Tao sheet
			Map<String, XSSFCellStyle> style = ExcelPOIProcessUtils.createStyles(workbook);

			SXSSFSheet sheetData = (SXSSFSheet) workbook.createSheet("Danh_sach_ctkm");
			//Set Getting Defaul
			sheetData.setDefaultRowHeight((short) (15 * 20));
			sheetData.setDefaultColumnWidth(13);
			//set static Column width
			ExcelPOIProcessUtils.setColumnsWidth(sheetData, 0, 125, 350, 250, 125, 125, 125);
			//Size Row
			ExcelPOIProcessUtils.setRowsHeight(sheetData, 0, 15, 25, 15, 15);
			sheetData.setDisplayGridlines(false);

			/** */
			// header
			int colIdx = 0;
			int rowIdx = 0;
			// title
			rowIdx++;
			String titleTemp = Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "promotion.program.export.title");
			ExcelPOIProcessUtils.addCellsAndMerged(sheetData, 0, rowIdx, 5, rowIdx++, titleTemp, style.get(ExcelPOIProcessUtils.TITLE_VNM_BLACK));

			rowIdx++;
			String[] menu = R.getResource("promotion.program.export.menu").split(";");

			for (int i = 0; i < menu.length; i++) {
				ExcelPOIProcessUtils.addCell(sheetData, colIdx++, rowIdx, menu[i], style.get(ExcelPOIProcessUtils.HEADER_BLUE_TOP_BOTTOM_MEDIUM));
			}
			// data
			rowIdx++;
			colIdx = 0;
			PromotionProgram vo = null;
			String action = R.getResource("action.status.name");
			String stop = R.getResource("pause.status.name");
			String wait = R.getResource("action.status.waiting");
			String expire = R.getResource("action.status.expire");

			for (int i = 0, size = lst.size(); i < size; i++, rowIdx++) {
				vo = lst.get(i);
				colIdx = 0;
				//ExcelPOIProcessUtils.setRowsHeight(sheetData, rowIdx, 15);
				ExcelPOIProcessUtils.addCell(sheetData, colIdx++, rowIdx, vo.getPromotionProgramCode(), style.get(ExcelPOIProcessUtils.ROW_DOTTED_LEFT));
				ExcelPOIProcessUtils.addCell(sheetData, colIdx++, rowIdx, vo.getPromotionProgramName(), style.get(ExcelPOIProcessUtils.ROW_DOTTED_LEFT));
				ExcelPOIProcessUtils.addCell(sheetData, colIdx++, rowIdx, vo.getType() != null ? vo.getType() + " - " + vo.getProFormat() : "", style.get(ExcelPOIProcessUtils.ROW_DOTTED_LEFT));
				ExcelPOIProcessUtils.addCell(sheetData, colIdx++, rowIdx, vo.getFromDate() != null ? DateUtil.toDateString(vo.getFromDate(), DateUtil.DATE_FORMAT_DDMMYYYY) : "", style.get(ExcelPOIProcessUtils.ROW_DOTTED_CENTER));
				ExcelPOIProcessUtils.addCell(sheetData, colIdx++, rowIdx, vo.getToDate() != null ? DateUtil.toDateString(vo.getToDate(), DateUtil.DATE_FORMAT_DDMMYYYY) : "", style.get(ExcelPOIProcessUtils.ROW_DOTTED_CENTER));

				if (ActiveType.RUNNING.equals(vo.getStatus())) {
					if (vo.getFlagStatusExpire() != null && ActiveType.HET_HAN.getValue().equals(vo.getFlagStatusExpire())) {
						ExcelPOIProcessUtils.addCell(sheetData, colIdx++, rowIdx, expire, style.get(ExcelPOIProcessUtils.ROW_DOTTED_CENTER));
					} else {
						ExcelPOIProcessUtils.addCell(sheetData, colIdx++, rowIdx, action, style.get(ExcelPOIProcessUtils.ROW_DOTTED_CENTER));
					}
				} else if (ActiveType.STOPPED.equals(vo.getStatus())) {
					ExcelPOIProcessUtils.addCell(sheetData, colIdx++, rowIdx, stop, style.get(ExcelPOIProcessUtils.ROW_DOTTED_CENTER));
				} else {
					ExcelPOIProcessUtils.addCell(sheetData, colIdx++, rowIdx, wait, style.get(ExcelPOIProcessUtils.ROW_DOTTED_CENTER));
				}

			}

			out = new FileOutputStream(Configuration.getStoreRealPath() + fileName);
			workbook.write(out);
			result.put(ERROR, false);
			result.put("hasData", true);
			String filePath = Configuration.getExportExcelPath() + fileName;
			result.put(REPORT_PATH, filePath);
			MemcachedUtils.putValueToMemcached(reportToken, filePath, retrieveReportMemcachedTimeout());
			lst = null;
			//			System.gc();
		} catch (Exception ex) {
			LogUtility.logErrorStandard(ex, R.getResource("web.log.message.error", "vnm.web.action.program.PromotionCatalogAction.export"), createLogErrorStandard(actionStartTime));
			result.put("errMsg", ValidateUtil.getErrorMsg(ConstantManager.ERR_SYSTEM));
			result.put(ERROR, true);
		} finally {
			if (workbook != null) {
				workbook.dispose();
			}
			if (out != null) {
				out.close();
			}
		}
		return JSON;
	}

	/**
	 * Xu ly searchShopNPP CTKM tu dong
	 * 
	 * @author vuongmq
	 * @return String
	 * @since Apr 25, 2016
	 */
	public String searchShopNPP() {
		Date startLogDate = DateUtil.now();
		result.put("page", page);
		result.put("max", max);
		try {
			BasicFilter<ShopVO> filter = new BasicFilter<ShopVO>();
			KPaging<ShopVO> kPaging = new KPaging<ShopVO>();
			kPaging.setPageSize(max);
			kPaging.setPage(page - 1);
			filter.setkPaging(kPaging);
			filter.setStatus(ActiveType.RUNNING.getValue());
			if (!StringUtil.isNullOrEmpty(code)) {
				filter.setCode(code);
			}
			if (!StringUtil.isNullOrEmpty(name)) {
				filter.setName(name);
			}
			if (promotionId != null) {
				filter.setObjectId(promotionId);
			}
			filter.setIntFlag(ShopDecentralizationSTT.NPP.getValue()); // get NPP GT va MT
			filter.setShopRootId(currentUser.getShopRoot().getShopId());
			filter.setStrShopId(super.getStrListShopId());
			ObjectVO<ShopVO> data = shopMgr.getListShopVONPPFilter(filter);
			if (data != null && !data.getLstObject().isEmpty()) {
				result.put("total", data.getkPaging().getTotalRows());
				result.put("rows", data.getLstObject());
			} else {
				result.put("total", 0);
				result.put("rows", new ArrayList<ShopVO>());
			}
		} catch (Exception e) {
			LogUtility.logErrorStandard(e, R.getResource("web.log.message.error", "vnm.web.action.program.ProgramCatalogAction.searchShopNPP()"), createLogErrorStandard(startLogDate));
			result.put("errMsg", ValidateUtil.getErrorMsg(ConstantManager.ERR_SYSTEM));
			result.put(ERROR, true);
		}
		return JSON;
	}
	public String newDeleteSubLevelKM1() {
		actionStartTime = new Date();
		resetToken(result);
		try {
			ObjectVO<NewLevelMapping> vo = promotionProgramMgr.getListMappingLevel(groupMuaId, groupKMId, fromLevel, toLevel);
			listNewMapping = vo.getLstObject();
			result.put("listNewMapping", listNewMapping);
			PromotionProgram program = promotionProgramMgr.newDeleteSubLevel(levelId, getLogInfoVO());
			if (program != null) {
				promotionProgramMgr.updateMD5ValidCode(program, getLogInfoVO());
			}
			result.put(ERROR, false);
		} catch (Exception e) {
			LogUtility.logErrorStandard(e, R.getResource("web.log.message.error", "vnm.web.action.program.PromotionCatalogAction.newDeleteSubLevel"), createLogErrorStandard(actionStartTime));
			result.put("errMsg", ValidateUtil.getErrorMsg(ConstantManager.ERR_SYSTEM));
			result.put(ERROR, true);
		}
		return SUCCESS;
	}

	/**
	 * Xu ly saveShopSupportQuantity CTKM tu dong
	 * 
	 * @author vuongmq
	 * @return String
	 * @since Apr 26, 2016
	 */
	public String saveShopSupportQuantity() {
		List<PromotionShopMap> lstCurrentPromotionShopMap = new ArrayList<PromotionShopMap>();
		List<PromotionShopJoin> lstCurrentPromotionShopJoin = new ArrayList<PromotionShopJoin>();
		resetToken(result);
		actionStartTime = DateUtil.now();
		result.put(ERROR, true);
		try {
			if (currentUser == null || currentUser.getShopRoot() == null) {
				result.put("errMsg", ValidateUtil.getErrorMsg(ConstantManager.ERR_NOT_PERMISSION, ""));
				return JSON;
			}
			if (promotionId == null || promotionId < 1 || lstShopQttAdd == null || lstShopQttAdd.size() == 0) {
				result.put(ERR_MSG, ValidateUtil.getErrorMsg(ConstantManager.ERR_NOT_DATA_CORRECT));
				return JSON;
			}
			PromotionProgram pro = promotionProgramMgr.getPromotionProgramById(promotionId);
			if (pro == null) {
				result.put(ERR_MSG, ValidateUtil.getErrorMsg(ConstantManager.ERR_NOT_EXIST_DB, "CTKM"));
				return JSON;
			}
			if (!ActiveType.RUNNING.equals(pro.getStatus())) {
				result.put(ERR_MSG, Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "promotion.program.incorrect"));
				return JSON;
			}
			lstCurrentPromotionShopMap = promotionProgramMgr.getListPromotionChildShopMapWithShopAndPromotionProgram(currentUser.getShopRoot().getShopId(), promotionId);
			lstCurrentPromotionShopJoin = promotionProgramMgr.getListPromotionChildShopJoinWithShopAndPromotionProgram(currentUser.getShopRoot().getShopId(), promotionId);
			List<PromotionShopMap> lstNewShopMap = new ArrayList<PromotionShopMap>();
			List<PromotionShopMap> lstNewShopMapNewTree = new ArrayList<PromotionShopMap>(); // chua shop nam trong cay moi
			List<PromotionShopMap> lstNewShopMapOldTree = new ArrayList<PromotionShopMap>(); // chua shop nam trong cay da them
			List<PromotionShopJoin> lstNewShopJoin = new ArrayList<PromotionShopJoin>();
			PromotionShopMap psmAdd = null;
			Shop shT = null;
			Date now = commonMgr.getSysDate();
			Long idt = null;
			for (int i = 0, sz = lstShopQttAdd.size(); i < sz; i++) {
				PromotionShopQttVO promotionShopQttVO = lstShopQttAdd.get(i);
				if (promotionShopQttVO != null) {
					idt = promotionShopQttVO.getShopId();
					if (super.getMapShopChild().get(idt) == null) {
						result.put(ERR_MSG, R.getResource("common.cms.shop.undefined"));
						return JSON;
					}
					shT = shopMgr.getShopById(idt);
					if (shT == null || shT.getType() == null || (!ShopSpecificType.NPP.equals(shT.getType().getSpecificType()) && !ShopSpecificType.NPP_MT.equals(shT.getType().getSpecificType()))) {
						result.put(ERR_MSG, R.getResource("common.cms.shop.undefined"));
						return JSON;
					}
					if (!ActiveType.RUNNING.equals(shT.getStatus())) {
						result.put(ERR_MSG, ValidateUtil.getErrorMsg(ConstantManager.ERR_STATUS_INACTIVE, shT.getShopCode()));
						return JSON;
					}
					PromotionShopMap psm = promotionProgramMgr.getPromotionShopMap(idt, promotionId);
					if (psm != null && ActiveType.RUNNING.equals(psm.getStatus())) {
						result.put(ERR_MSG, Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "promotion.shop.exist.shop.map.view", shT.getShopCode()));
						return JSON;
					}
					psmAdd = new PromotionShopMap();
					psmAdd.setPromotionProgram(pro);
					psmAdd.setShop(shT);
					psmAdd.setStatus(ActiveType.RUNNING);
					psmAdd.setCreateDate(now);
					psmAdd.setCreateUser(currentUser.getUserName());
					psmAdd.setFromDate(pro.getFromDate());
					psmAdd.setToDate(pro.getToDate());
					if (promotionShopQttVO.getQuantityMax() != null) {
						if (!ValidateUtil.validateNumber(promotionShopQttVO.getQuantityMax().toString()) || promotionShopQttVO.getQuantityMax().intValue() < 0) {
							result.put(ERR_MSG, R.getResource("catalog.promotion.shop.add.map.quantity.max.error", shT.getShopCode()));
							return JSON;
						}
						psmAdd.setQuantityMax(promotionShopQttVO.getQuantityMax());
					}
					if (promotionShopQttVO.getNumMax() != null) {
						if (!ValidateUtil.validateNumber(promotionShopQttVO.getNumMax().toString()) || promotionShopQttVO.getNumMax().intValue() < 0) {
							result.put(ERR_MSG, R.getResource("catalog.promotion.shop.add.map.num.max.error", shT.getShopCode()));
							return JSON;
						}
						psmAdd.setNumMax(promotionShopQttVO.getNumMax());
					}
					if (promotionShopQttVO.getAmountMax() != null) {
						if (!ValidateUtil.validateNumber(promotionShopQttVO.getAmountMax().toString()) || promotionShopQttVO.getAmountMax().intValue() < 0) {
							result.put(ERR_MSG, R.getResource("catalog.promotion.shop.add.map.amount.max.error", shT.getShopCode()));
							return JSON;
						}
						psmAdd.setAmountMax(promotionShopQttVO.getAmountMax());
					}
					// ktra co cha phan bo k
					List<Shop> listParentShop = shopMgr.getListParentShopId(ActiveType.RUNNING.getValue(), idt);
					PromotionShopJoin parentAllotion = getParentPromotionShopJoinAllcation(listParentShop, lstCurrentPromotionShopJoin);
					if (parentAllotion == null) {
						lstNewShopMapNewTree.add(psmAdd);
					} else {
						lstNewShopMapOldTree.add(psmAdd);
						// them cac shop cha con thieu
						for (Shop shop : listParentShop) {
							if (shop.getId() != null && parentAllotion.getShop() != null && shop.getId().equals(parentAllotion.getShop().getId())) {
								break;
							}
							PromotionShopJoin shopAdded = getParentPromotionShopJoin(lstNewShopJoin, shop.getId());
							if (shopAdded != null) {
								shopAdded.setQuantityMax(shopAdded.getQuantityMax() == null ? promotionShopQttVO.getQuantityMax() : (shopAdded.getQuantityMax() + promotionShopQttVO.getQuantityMax()));
								shopAdded.setNumMax(shopAdded.getNumMax() == null ? promotionShopQttVO.getNumMax() : shopAdded.getNumMax().add(promotionShopQttVO.getNumMax()));
								shopAdded.setAmountMax(shopAdded.getAmountMax() == null ? promotionShopQttVO.getAmountMax() : shopAdded.getAmountMax().add(promotionShopQttVO.getAmountMax()));
							} else {
								PromotionShopJoin newShopJoin = new PromotionShopJoin();
								newShopJoin.setPromotionProgram(pro);
								newShopJoin.setShop(shop);
								newShopJoin.setStatus(ActiveType.RUNNING);
								newShopJoin.setCreateDate(now);
								newShopJoin.setCreateUser(currentUser.getUserName());
								newShopJoin.setFromDate(pro.getFromDate());
								newShopJoin.setToDate(pro.getToDate());
								newShopJoin.setQuantityMax(promotionShopQttVO.getQuantityMax());
								newShopJoin.setNumMax(promotionShopQttVO.getNumMax());
								newShopJoin.setAmountMax(promotionShopQttVO.getAmountMax());
								lstNewShopJoin.add(newShopJoin);
							}
						}
					}
					lstNewShopMap.add(psmAdd);
				}
			}
			// validate cac shop thuoc nhanh moi
			if (lstNewShopMapNewTree.size() > 0) {
				// lay cau hinh bat buoc cac node NPP phai duoc phan bo
				List<ApParam> allocationPromotionShopConfigs = apParamMgr.getListApParam(ApParamType.ALLOCATION_PROMOTION_SHOP, ActiveType.RUNNING);
				isAllocationPromotionShop = (allocationPromotionShopConfigs == null || allocationPromotionShopConfigs.size() == 0 || Constant.ONE_TEXT.equals(allocationPromotionShopConfigs.get(0).getValue()));
				if (isAllocationPromotionShop) {
					errMsg = validateQuantityAmountNumPromotionShop(lstCurrentPromotionShopMap, lstNewShopMapNewTree);
					if (!StringUtil.isNullOrEmpty(errMsg)) {
						result.put("errMsg", errMsg);
						result.put(ERROR, true);
						return JSON;
					}
				}
			}
			// validate cac shop thuoc nhanh da them
			if (lstNewShopMapOldTree.size() > 0) {
				lstCurrentPromotionShopMap.addAll(lstNewShopMapOldTree);
				lstCurrentPromotionShopJoin.addAll(lstNewShopJoin);
				errMsg = validateQuantityAmountNumPromotionShop(lstCurrentPromotionShopMap, lstNewShopMapOldTree);
				if (!StringUtil.isNullOrEmpty(errMsg)) {
					result.put("errMsg", errMsg);
					result.put(ERROR, true);
					return JSON;
				}
				errMsg = validateAllocateParentChildPromotionShop(lstCurrentPromotionShopMap, lstCurrentPromotionShopJoin);
				if (!StringUtil.isNullOrEmpty(errMsg)) {
					result.put("errMsg", errMsg);
					result.put(ERROR, true);
					return JSON;
				}
				errMsg = validatePromotionShop(lstCurrentPromotionShopMap, lstCurrentPromotionShopJoin);
				if (!StringUtil.isNullOrEmpty(errMsg)) {
					result.put("errMsg", errMsg);
					result.put(ERROR, true);
					return JSON;
				}
			}
			promotionProgramMgr.addPromotionShop(lstNewShopMap, lstNewShopJoin, getLogInfoVO());
			result.put(ERROR, false);
		} catch (BusinessException ex) {
			LogUtility.logErrorStandard(ex, "vnm.web.action.program.ProgramCatalogAction.saveShopSupportQuantity", createLogErrorStandard(actionStartTime));
			result.put(ERROR, true);
			result.put(ERR_MSG, ValidateUtil.getErrorMsg(ConstantManager.ERR_SYSTEM));
		} finally {
			lstCurrentPromotionShopMap.clear();
			lstCurrentPromotionShopMap = null;
			lstCurrentPromotionShopJoin.clear();
			lstCurrentPromotionShopJoin = null;
		}
		return JSON;
	}

	private PromotionShopJoin getParentPromotionShopJoinAllcation(List<Shop> listParentShop, List<PromotionShopJoin> lstPromotionShopJoin) {
		for (int i = 0, size = listParentShop.size(); i < size; i++) {
			Shop parent = listParentShop.get(i);
			PromotionShopJoin promotionShopJoin = getParentPromotionShopJoin(lstPromotionShopJoin, parent.getId());
			if (promotionShopJoin != null) {
				return promotionShopJoin;
			}
		}
		return null;
	}

	/**
	 * Neu cha phan bo thi tat ca con phai duoc phan bo
	 */
	private String validateAllocateParentChildPromotionShop(List<PromotionShopMap> lstPromotionShopMap, List<PromotionShopJoin> lstPromotionShopJoin) {
		for (int i = 0, size = lstPromotionShopJoin.size(); i < size; i++) {
			PromotionShopJoin parent = lstPromotionShopJoin.get(i);
			if (parent.getShop() != null && parent.getQuantityMax() != null || parent.getAmountMax() != null || parent.getNumMax() != null) {
				for (int j = i, n = lstPromotionShopJoin.size(); j < n; j++) {
					PromotionShopJoin child = lstPromotionShopJoin.get(j);
					Shop childShop = child.getShop();
					if (childShop != null && childShop.getParentShop() != null && childShop.getParentShop().getId().equals(parent.getShop().getId())) {
						if (parent.getQuantityMax() != null && child.getQuantityMax() == null) {
							return Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "promotion.program.shop.no.allcate.quantity", childShop.getShopCode() + " - " + childShop.getShopName());
						}
						if (parent.getAmountMax() != null && child.getAmountMax() == null) {
							return Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "promotion.program.shop.no.allcate.amount", childShop.getShopCode() + " - " + childShop.getShopName());
						}
						if (parent.getNumMax() != null && child.getNumMax() == null) {
							return Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "promotion.program.shop.no.allcate.num", childShop.getShopCode() + " - " + childShop.getShopName());
						}
					}
				}
				for (int j = i, n = lstPromotionShopMap.size(); j < n; j++) {
					PromotionShopMap child = lstPromotionShopMap.get(j);
					Shop childShop = child.getShop();
					if (childShop != null && childShop.getParentShop() != null && childShop.getParentShop().getId().equals(parent.getShop().getId())) {
						if (parent.getQuantityMax() != null && child.getQuantityMax() == null) {
							return Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "promotion.program.shop.no.allcate.quantity", childShop.getShopCode() + " - " + childShop.getShopName());
						}
						if (parent.getAmountMax() != null && child.getAmountMax() == null) {
							return Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "promotion.program.shop.no.allcate.amount", childShop.getShopCode() + " - " + childShop.getShopName());
						}
						if (parent.getNumMax() != null && child.getNumMax() == null) {
							return Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "promotion.program.shop.no.allcate.num", childShop.getShopCode() + " - " + childShop.getShopName());
						}
					}
				}
			}
		}
		return "";
	}

	/**
	 * Kiem tra phai co phan bo so suat/so tien/so luong
	 */
	private String validateQuantityAmountNumPromotionShop(List<PromotionShopMap> lstPromotionShopMap) {
		int size = lstPromotionShopMap.size();
		int ALL_ALLOCATE = size;
		int NO_ALLOCATE = 0;
		int allocateQuantity = 0;
		int allocateAmount = 0;
		int allocateNum = 0;
		for (int i = 0; i < size; i++) {
			PromotionShopMap psm = lstPromotionShopMap.get(i);
			if (psm.getQuantityMax() != null) {
				allocateQuantity++;
			}
			if (psm.getAmountMax() != null) {
				allocateAmount++;
			}
			if (psm.getNumMax() != null) {
				allocateNum++;
			}
		}
		// neu co phan bo thi phai phan bo tat ca cac dong
		if (NO_ALLOCATE < allocateQuantity && allocateQuantity < ALL_ALLOCATE) {
			return Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "promotion.program.shop.no.allcate.all.quantity");
		}
		if (NO_ALLOCATE < allocateAmount && allocateAmount < ALL_ALLOCATE) {
			return Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "promotion.program.shop.no.allcate.all.amount");
		}
		if (NO_ALLOCATE < allocateNum && allocateNum < ALL_ALLOCATE) {
			return Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "promotion.program.shop.no.allcate.all.num");
		}
		// phai phan bo it nhat 1 loai (so suat/so tien/so luong)
		if (allocateQuantity == NO_ALLOCATE && allocateAmount == NO_ALLOCATE && allocateNum == NO_ALLOCATE) {
			return Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "promotion.program.shop.no.allocate.all");
		}
		return "";
	}

	/**
	 * Kiem tra phai co phan bo so suat/so tien/so luong
	 */
	private String validateQuantityAmountNumPromotionShop(List<PromotionShopMap> lstCurrentPromotionShopMap, List<PromotionShopMap> lstNewPromotionShopMap) {
		int size = lstCurrentPromotionShopMap.size();
		int allocateQuantity = 0;
		int allocateAmount = 0;
		int allocateNum = 0;
		for (int i = 0; i < size; i++) {
			PromotionShopMap psm = lstCurrentPromotionShopMap.get(i);
			if (psm.getQuantityMax() != null) {
				allocateQuantity++;
			}
			if (psm.getAmountMax() != null) {
				allocateAmount++;
			}
			if (psm.getNumMax() != null) {
				allocateNum++;
			}
		}
		for (int j = 0, n = lstNewPromotionShopMap.size(); j < n; j++) {
			PromotionShopMap promotionShopMap = lstNewPromotionShopMap.get(j);
			String shopName = promotionShopMap.getShop() == null ? "" : (promotionShopMap.getShop().getShopCode() + " - " + promotionShopMap.getShop().getShopName());
			if (promotionShopMap.getQuantityMax() != null && allocateQuantity == 0) {
				return Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "promotion.program.shop.denied.allcate.quantity", shopName);
			}
			if (promotionShopMap.getQuantityMax() == null && allocateQuantity > 0) {
				return Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "promotion.program.shop.no.allcate.quantity", shopName);
			}
			if (promotionShopMap.getAmountMax() != null && allocateAmount == 0) {
				return Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "promotion.program.shop.denied.allcate.amount", shopName);
			}
			if (promotionShopMap.getAmountMax() == null && allocateAmount > 0) {
				return Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "promotion.program.shop.no.allcate.amount", shopName);
			}
			if (promotionShopMap.getNumMax() != null && allocateNum == 0) {
				return Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "promotion.program.shop.denied.allcate.num", shopName);
			}
			if (promotionShopMap.getNumMax() == null && allocateNum > 0) {
				return Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "promotion.program.shop.no.allcate.num", shopName);
			}
		}
		return "";
	}

	/**
	 * kiem tra trong phan bo node con <= node cha
	 */
	private String validatePromotionShop(List<PromotionShopMap> lstPromotionShopMap, List<PromotionShopJoin> lstPromotionShopJoin) {
		List<Long> listIdChecked = new ArrayList<Long>();
		// kiem tra tu NPP
		if (lstPromotionShopMap != null && lstPromotionShopMap.size() > 0) {
			for (PromotionShopMap psm : lstPromotionShopMap) {
				if (psm.getShop() != null && psm.getShop().getParentShop() != null && psm.getShop().getParentShop().getId() != null) {
					Shop shopParent = psm.getShop().getParentShop();
					Long parentId = shopParent.getId();
					if (!listIdChecked.contains(parentId)) { // nhom con cung cha da kiem tra chua?
						listIdChecked.add(parentId);
						PromotionShopJoin parent = this.getParentPromotionShopJoin(lstPromotionShopJoin, parentId);
						if (parent != null) {
							Integer quantityMaxSumChild = 0;
							BigDecimal amountMaxSumChild = BigDecimal.ZERO;
							BigDecimal numMaxSumChild = BigDecimal.ZERO;
							for (PromotionShopMap child : lstPromotionShopMap) {
								if (child.getShop() != null && child.getShop().getParentShop() != null && parentId.equals(child.getShop().getParentShop().getId())) {
									if (child.getQuantityMax() != null) {
										quantityMaxSumChild += child.getQuantityMax().intValue();
									}
									if (child.getAmountMax() != null) {
										amountMaxSumChild = amountMaxSumChild.add(child.getAmountMax());
									}
									if (child.getNumMax() != null) {
										numMaxSumChild = numMaxSumChild.add(child.getNumMax());
									}
								}
							}
							String error = compareParentPromotionShopJoin(parent, quantityMaxSumChild, amountMaxSumChild, numMaxSumChild);
							if (!StringUtil.isNullOrEmpty(error)) {
								return error;
							}
						}
					}
				}
			}
		}
		// kiem tra cac don vi cha
		if (lstPromotionShopJoin != null && lstPromotionShopJoin.size() > 0) {
			for (PromotionShopJoin psj : lstPromotionShopJoin) {
				if (psj.getShop() != null && psj.getShop().getParentShop() != null && psj.getShop().getParentShop().getId() != null) {
					Shop shopParent = psj.getShop().getParentShop();
					Long parentId = shopParent.getId();
					if (!listIdChecked.contains(parentId)) { // nhom con cung cha da kiem tra chua?
						listIdChecked.add(parentId);
						PromotionShopJoin parent = this.getParentPromotionShopJoin(lstPromotionShopJoin, parentId);
						if (parent != null) {
							Integer quantityMaxSumChild = 0;
							BigDecimal amountMaxSumChild = BigDecimal.ZERO;
							BigDecimal numMaxSumChild = BigDecimal.ZERO;
							for (PromotionShopJoin child : lstPromotionShopJoin) {
								if (child.getShop() != null && child.getShop().getParentShop() != null && parentId.equals(child.getShop().getParentShop().getId())) {
									if (child.getQuantityMax() != null) {
										quantityMaxSumChild += child.getQuantityMax().intValue();
									}
									if (child.getAmountMax() != null) {
										amountMaxSumChild = amountMaxSumChild.add(child.getAmountMax());
									}
									if (child.getNumMax() != null) {
										numMaxSumChild = numMaxSumChild.add(child.getNumMax());
									}
								}
							}
							String error = compareParentPromotionShopJoin(parent, quantityMaxSumChild, amountMaxSumChild, numMaxSumChild);
							if (!StringUtil.isNullOrEmpty(error)) {
								return error;
							}
						}
					}
				}
			}
		}
		return "";
	}

	private PromotionShopJoin getParentPromotionShopJoin(List<PromotionShopJoin> lstPromotionShopJoin, Long parentId) {
		for (PromotionShopJoin psj : lstPromotionShopJoin) {
			if (psj.getShop() != null && psj.getShop().getId() != null && psj.getShop().getId().equals(parentId)) {
				return psj;
			}
		}
		return null;
	}

	private String compareParentPromotionShopJoin(PromotionShopJoin parent, int quantityMaxSumChild, BigDecimal amountMaxSumChild, BigDecimal numMaxSumChild) {
		if (parent.getQuantityMax() != null && quantityMaxSumChild > parent.getQuantityMax().intValue()) {
			return Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "promotion.program.sum.quantity.chid.shop.can.not.more.than.parent", parent.getShop() == null ? ""
					: (parent.getShop().getShopCode() + " - " + parent.getShop().getShopName()));
		}
		if (parent.getAmountMax() != null && amountMaxSumChild.compareTo(parent.getAmountMax()) > 0) {
			return Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "promotion.program.sum.amount.chid.shop.can.not.more.than.parent", parent.getShop() == null ? ""
					: (parent.getShop().getShopCode() + " - " + parent.getShop().getShopName()));
		}
		if (parent.getNumMax() != null && numMaxSumChild.compareTo(parent.getNumMax()) > 0) {
			return Configuration.getResourceString(ConstantManager.VI_LANGUAGE, "promotion.program.sum.num.chid.shop.can.not.more.than.parent", parent.getShop() == null ? ""
					: (parent.getShop().getShopCode() + " - " + parent.getShop().getShopName()));
		}
		return "";
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public Long getShopId() {
		return shopId;
	}

	public void setShopId(Long shopId) {
		this.shopId = shopId;
	}

	public Long getPromotionId() {
		return promotionId;
	}

	public void setPromotionId(Long promotionId) {
		this.promotionId = promotionId;
	}

	public List<Long> getLstId() {
		return lstId;
	}

	public void setLstId(List<Long> lstId) {
		this.lstId = lstId;
	}

	public List<TreeGridNode<PromotionShopVO>> getLstTree() {
		return lstTree;
	}

	public void setLstTree(List<TreeGridNode<PromotionShopVO>> lstTree) {
		this.lstTree = lstTree;
	}

	public List<Integer> getLstQtt() {
		return lstQtt;
	}

	public void setLstQtt(List<Integer> lstQtt) {
		this.lstQtt = lstQtt;
	}

	public List<Boolean> getLstEdit() {
		return lstEdit;
	}

	public void setLstEdit(List<Boolean> lstEdit) {
		this.lstEdit = lstEdit;
	}

	public String getFromDate() {
		return fromDate;
	}

	public void setFromDate(String fromDate) {
		this.fromDate = fromDate;
	}

	public String getToDate() {
		return toDate;
	}

	public void setToDate(String toDate) {
		this.toDate = toDate;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getShopCode() {
		return shopCode;
	}

	public void setShopCode(String shopCode) {
		this.shopCode = shopCode;
	}

	public Integer getProType() {
		return proType;
	}

	public void setProType(Integer proType) {
		this.proType = proType;
	}

	public PromotionProgram getPromotionProgram() {
		return promotionProgram;
	}

	public void setPromotionProgram(PromotionProgram promotionProgram) {
		this.promotionProgram = promotionProgram;
	}

	public String getLstTypeId() {
		return lstTypeId;
	}

	public void setLstTypeId(String lstTypeId) {
		this.lstTypeId = lstTypeId;
	}

	public List<PromotionCustAttrVO> getLstPromotionCustAttrVO() {
		return lstPromotionCustAttrVO;
	}

	public void setLstPromotionCustAttrVO(List<PromotionCustAttrVO> lstPromotionCustAttrVO) {
		this.lstPromotionCustAttrVO = lstPromotionCustAttrVO;
	}

	public List<Integer> getLstObjectType() {
		return lstObjectType;
	}

	public void setLstObjectType(List<Integer> lstObjectType) {
		this.lstObjectType = lstObjectType;
	}

	public List<Long> getLstCustomerType() {
		return lstCustomerType;
	}

	public void setLstCustomerType(List<Long> lstCustomerType) {
		this.lstCustomerType = lstCustomerType;
	}

	public List<Long> getLstSaleLevelCatId() {
		return lstSaleLevelCatId;
	}

	public void setLstSaleLevelCatId(List<Long> lstSaleLevelCatId) {
		this.lstSaleLevelCatId = lstSaleLevelCatId;
	}

	public List<String> getLstAttDataInField() {
		return lstAttDataInField;
	}

	public void setLstAttDataInField(List<String> lstAttDataInField) {
		this.lstAttDataInField = lstAttDataInField;
	}

	public List<TreeGridNode<PromotionStaffVO>> getLstStaffTree() {
		return lstStaffTree;
	}

	public void setLstStaffTree(List<TreeGridNode<PromotionStaffVO>> lstStaffTree) {
		this.lstStaffTree = lstStaffTree;
	}

	public File getExcelFile() {
		return excelFile;
	}

	public void setExcelFile(File excelFile) {
		this.excelFile = excelFile;
	}

	public String getExcelFileContentType() {
		return excelFileContentType;
	}

	public void setExcelFileContentType(String excelFileContentType) {
		this.excelFileContentType = excelFileContentType;
	}

	public List<CellBean> getLstHeaderError() {
		return lstHeaderError;
	}

	public void setLstHeaderError(List<CellBean> lstHeaderError) {
		this.lstHeaderError = lstHeaderError;
	}

	public List<CellBean> getLstDetailError() {
		return lstDetailError;
	}

	public void setLstDetailError(List<CellBean> lstDetailError) {
		this.lstDetailError = lstDetailError;
	}

	public List<ApParam> getLstTypeCode() {
		return lstTypeCode;
	}

	public void setLstTypeCode(List<ApParam> lstTypeCode) {
		this.lstTypeCode = lstTypeCode;
	}

	public Boolean getIsVNMAdmin() {
		return isVNMAdmin;
	}

	public void setIsVNMAdmin(Boolean isVNMAdmin) {
		this.isVNMAdmin = isVNMAdmin;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getGroupCode() {
		return groupCode;
	}

	public void setGroupCode(String groupCode) {
		this.groupCode = groupCode;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public Integer getMaxQuantity() {
		return maxQuantity;
	}

	public void setMaxQuantity(Integer maxQuantity) {
		this.maxQuantity = maxQuantity;
	}

	public BigDecimal getMaxAmount() {
		return maxAmount;
	}

	public void setMaxAmount(BigDecimal maxAmount) {
		this.maxAmount = maxAmount;
	}

	public void setMinAmount(BigDecimal minAmount) {
		this.minAmount = minAmount;
	}

	public List<ProductGroup> getLstGroupSale() {
		return lstGroupSale;
	}

	public void setLstGroupSale(List<ProductGroup> lstGroupSale) {
		this.lstGroupSale = lstGroupSale;
	}

	public List<ProductGroup> getLstGroupFree() {
		return lstGroupFree;
	}

	public void setLstGroupFree(List<ProductGroup> lstGroupFree) {
		this.lstGroupFree = lstGroupFree;
	}

	public List<ExcelPromotionHeader> getListHeader() {
		return listHeader;
	}

	public void setListHeader(List<ExcelPromotionHeader> listHeader) {
		this.listHeader = listHeader;
	}

	public List<ExcelPromotionDetail> getListDetail() {
		return listDetail;
	}

	public void setListDetail(List<ExcelPromotionDetail> listDetail) {
		this.listDetail = listDetail;
	}

	public Map<String, ListGroupMua> getMapPromotionMua() {
		return mapPromotionMua;
	}

	public void setMapPromotionMua(Map<String, ListGroupMua> mapPromotionMua) {
		this.mapPromotionMua = mapPromotionMua;
	}

	public Map<String, ListGroupKM> getMapPromotionKM() {
		return mapPromotionKM;
	}

	public void setMapPromotionKM(Map<String, ListGroupKM> mapPromotionKM) {
		this.mapPromotionKM = mapPromotionKM;
	}

	public Map<String, String> getMapPromotionTypeCheck() {
		return mapPromotionTypeCheck;
	}

	public void setMapPromotionTypeCheck(Map<String, String> mapPromotionTypeCheck) {
		this.mapPromotionTypeCheck = mapPromotionTypeCheck;
	}

	public MapMuaKM getMapMuaKM() {
		return mapMuaKM;
	}

	public void setMapMuaKM(MapMuaKM mapMuaKM) {
		this.mapMuaKM = mapMuaKM;
	}

	public Boolean getMultiple() {
		return multiple;
	}

	public void setMultiple(Boolean multiple) {
		this.multiple = multiple;
	}

	public Boolean getRecursive() {
		return recursive;
	}

	public void setRecursive(Boolean recursive) {
		this.recursive = recursive;
	}

	public Integer getQuantityUnit() {
		return quantityUnit;
	}

	public void setQuantityUnit(Integer quantityUnit) {
		this.quantityUnit = quantityUnit;
	}

	public Integer getStt() {
		return stt;
	}

	public void setStt(Integer stt) {
		this.stt = stt;
	}

	public BigDecimal getMinQuantity() {
		return minQuantity;
	}

	public void setMinQuantity(BigDecimal minQuantity) {
		this.minQuantity = minQuantity;
	}

	public BigDecimal getMinAmount() {
		return minAmount;
	}

	public Long getGroupId() {
		return groupId;
	}

	public void setGroupId(Long groupId) {
		this.groupId = groupId;
	}

	public List<GroupLevelVO> getLstLevel() {
		return lstLevel;
	}

	public void setLstLevel(List<GroupLevelVO> lstLevel) {
		this.lstLevel = lstLevel;
	}

	public List<Product> getListProduct() {
		return listProduct;
	}

	public void setListProduct(List<Product> listProduct) {
		this.listProduct = listProduct;
	}

	public List<Integer> getListMinQuantity() {
		return listMinQuantity;
	}

	public void setListMinQuantity(List<Integer> listMinQuantity) {
		this.listMinQuantity = listMinQuantity;
	}

	public List<BigDecimal> getListMinAmount() {
		return listMinAmount;
	}

	public void setListMinAmount(List<BigDecimal> listMinAmount) {
		this.listMinAmount = listMinAmount;
	}

	public List<Integer> getListOrder() {
		return listOrder;
	}

	public void setListOrder(List<Integer> listOrder) {
		this.listOrder = listOrder;
	}

	public List<String> getListProductDetail() {
		return listProductDetail;
	}

	public void setListProductDetail(List<String> listProductDetail) {
		this.listProductDetail = listProductDetail;
	}

	public String getPromotionCode() {
		return promotionCode;
	}

	public void setPromotionCode(String promotionCode) {
		this.promotionCode = promotionCode;
	}

	public String getPromotionName() {
		return promotionName;
	}

	public void setPromotionName(String promotionName) {
		this.promotionName = promotionName;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public String getTypeCode() {
		return typeCode;
	}

	public void setTypeCode(String typeCode) {
		this.typeCode = typeCode;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Boolean getCanEdit() {
		return canEdit;
	}

	public void setCanEdit(Boolean canEdit) {
		this.canEdit = canEdit;
	}

	public List<Integer> getListMaxQuantity() {
		return listMaxQuantity;
	}

	public void setListMaxQuantity(List<Integer> listMaxQuantity) {
		this.listMaxQuantity = listMaxQuantity;
	}

	public List<BigDecimal> getListMaxAmount() {
		return listMaxAmount;
	}

	public void setListMaxAmount(List<BigDecimal> listMaxAmount) {
		this.listMaxAmount = listMaxAmount;
	}

	public Long getGroupMuaId() {
		return groupMuaId;
	}

	public void setGroupMuaId(Long groupMuaId) {
		this.groupMuaId = groupMuaId;
	}

	public Long getGroupKMId() {
		return groupKMId;
	}

	public void setGroupKMId(Long groupKMId) {
		this.groupKMId = groupKMId;
	}

	public List<LevelMappingVO> getListLevelMapping() {
		return listLevelMapping;
	}

	public void setListLevelMapping(List<LevelMappingVO> listLevelMapping) {
		this.listLevelMapping = listLevelMapping;
	}

	public List<Float> getListPercent() {
		return listPercent;
	}

	public void setListPercent(List<Float> listPercent) {
		this.listPercent = listPercent;
	}

	public Long getMappingId() {
		return mappingId;
	}

	public void setMappingId(Long mappingId) {
		this.mappingId = mappingId;
	}

	public String getGroupMuaCode() {
		return groupMuaCode;
	}

	public void setGroupMuaCode(String groupMuaCode) {
		this.groupMuaCode = groupMuaCode;
	}

	public Integer getOrderLevelMua() {
		return orderLevelMua;
	}

	public void setOrderLevelMua(Integer orderLevelMua) {
		this.orderLevelMua = orderLevelMua;
	}

	public String getGroupKMCode() {
		return groupKMCode;
	}

	public void setGroupKMCode(String groupKMCode) {
		this.groupKMCode = groupKMCode;
	}

	public Integer getOrderLevelKM() {
		return orderLevelKM;
	}

	public void setOrderLevelKM(Integer orderLevelKM) {
		this.orderLevelKM = orderLevelKM;
	}

	public List<Long> getListLevelId() {
		return listLevelId;
	}

	public void setListLevelId(List<Long> listLevelId) {
		this.listLevelId = listLevelId;
	}

	public Long getLevelMuaId() {
		return levelMuaId;
	}

	public void setLevelMuaId(Long levelMuaId) {
		this.levelMuaId = levelMuaId;
	}

	public Long getLevelKMId() {
		return levelKMId;
	}

	public void setLevelKMId(Long levelKMId) {
		this.levelKMId = levelKMId;
	}

	public Long getLevelDetailId() {
		return levelDetailId;
	}

	public void setLevelDetailId(Long levelDetailId) {
		this.levelDetailId = levelDetailId;
	}

	public String getPromotionType() {
		return promotionType;
	}

	public void setPromotionType(String promotionType) {
		this.promotionType = promotionType;
	}

	public Long getLevelId() {
		return levelId;
	}

	public void setLevelId(Long levelId) {
		this.levelId = levelId;
	}

	public String getLevelCode() {
		return levelCode;
	}

	public void setLevelCode(String levelCode) {
		this.levelCode = levelCode;
	}

	public Integer getCopyNum() {
		return copyNum;
	}

	public void setCopyNum(Integer copyNum) {
		this.copyNum = copyNum;
	}

	public List<NewLevelMapping> getListNewMapping() {
		return listNewMapping;
	}

	public void setListNewMapping(List<NewLevelMapping> listNewMapping) {
		this.listNewMapping = listNewMapping;
	}

	public Integer getMuaMinQuantity() {
		return muaMinQuantity;
	}

	public void setMuaMinQuantity(Integer muaMinQuantity) {
		this.muaMinQuantity = muaMinQuantity;
	}

	public BigDecimal getMuaMinAmount() {
		return muaMinAmount;
	}

	public void setMuaMinAmount(BigDecimal muaMinAmount) {
		this.muaMinAmount = muaMinAmount;
	}

	public Float getPercentKM() {
		return percentKM;
	}

	public void setPercentKM(Float percentKM) {
		this.percentKM = percentKM;
	}

	public Integer getKmMaxQuantity() {
		return kmMaxQuantity;
	}

	public void setKmMaxQuantity(Integer kmMaxQuantity) {
		this.kmMaxQuantity = kmMaxQuantity;
	}

	public BigDecimal getKmMaxAmount() {
		return kmMaxAmount;
	}

	public void setKmMaxAmount(BigDecimal kmMaxAmount) {
		this.kmMaxAmount = kmMaxAmount;
	}

	public List<ExMapping> getListSubLevelMua() {
		return listSubLevelMua;
	}

	public void setListSubLevelMua(List<ExMapping> listSubLevelMua) {
		this.listSubLevelMua = listSubLevelMua;
	}

	public List<ExMapping> getListSubLevelKM() {
		return listSubLevelKM;
	}

	
	public void setListSubLevelKM(List<ExMapping> listSubLevelKM) {
		this.listSubLevelKM = listSubLevelKM;
	}

	public List<NewProductGroupVO> getLstGroupNew() {
		return lstGroupNew;
	}

	public void setLstGroupNew(List<NewProductGroupVO> lstGroupNew) {
		this.lstGroupNew = lstGroupNew;
	}

	public List<PPConvertVO> getListConvertGroup() {
		return listConvertGroup;
	}

	public void setListConvertGroup(List<PPConvertVO> listConvertGroup) {
		this.listConvertGroup = listConvertGroup;
	}

	public List<PromotionProductOpenVO> getListProductOpen() {
		return listProductOpen;
	}

	public void setListProductOpen(List<PromotionProductOpenVO> listProductOpen) {
		this.listProductOpen = listProductOpen;
	}

	public Integer getQuantiMonthNewOpen() {
		return quantiMonthNewOpen;
	}

	public void setQuantiMonthNewOpen(Integer quantiMonthNewOpen) {
		this.quantiMonthNewOpen = quantiMonthNewOpen;
	}

	public Integer getIsEdited() {
		return isEdited;
	}

	public void setIsEdited(Integer isEdited) {
		this.isEdited = isEdited;
	}

	public Integer getPromotionStatus() {
		return promotionStatus;
	}

	public void setPromotionStatus(Integer promotionStatus) {
		this.promotionStatus = promotionStatus;
	}

	public Integer getFromLevel() {
		return fromLevel;
	}

	public void setFromLevel(Integer fromLevel) {
		this.fromLevel = fromLevel;
	}

	public Integer getToLevel() {
		return toLevel;
	}

	public void setToLevel(Integer toLevel) {
		this.toLevel = toLevel;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public BigDecimal getNumber() {
		return number;
	}

	public void setNumber(BigDecimal number) {
		this.number = number;
	}

	public List<BigDecimal> getLstAmt() {
		return lstAmt;
	}

	public void setLstAmt(List<BigDecimal> lstAmt) {
		this.lstAmt = lstAmt;
	}

	public List<BigDecimal> getLstNum() {
		return lstNum;
	}

	public void setLstNum(List<BigDecimal> lstNum) {
		this.lstNum = lstNum;
	}

	public String getFirstBuyType() {
		return firstBuyType;
	}

	public void setFirstBuyType(String firstBuyType) {
		this.firstBuyType = firstBuyType;
	}

	public Integer getFirstBuyNum() {
		return firstBuyNum;
	}

	public void setFirstBuyNum(Integer firstBuyNum) {
		this.firstBuyNum = firstBuyNum;
	}

	public Integer getNewCusNumCycle() {
		return newCusNumCycle;
	}

	public void setNewCusNumCycle(Integer newCusNumCycle) {
		this.newCusNumCycle = newCusNumCycle;
	}

	public Boolean getFirstBuyFlag() {
		return firstBuyFlag;
	}

	public void setFirstBuyFlag(Boolean firstBuyFlag) {
		this.firstBuyFlag = firstBuyFlag;
	}

	public Boolean getNewCusFlag() {
		return newCusFlag;
	}

	public void setNewCusFlag(Boolean newCusFlag) {
		this.newCusFlag = newCusFlag;
	}

	public Boolean getOntopFlag() {
		return ontopFlag;
	}

	public void setOntopFlag(Boolean ontopFlag) {
		this.ontopFlag = ontopFlag;
	}

	public List<PromotionShopQttVO> getLstShopQttAdd() {
		return lstShopQttAdd;
	}

	public void setLstShopQttAdd(List<PromotionShopQttVO> lstShopQttAdd) {
		this.lstShopQttAdd = lstShopQttAdd;
	}

	public Boolean getIsShowCompleteDefinePromo() {
		return isShowCompleteDefinePromo;
	}

	public void setIsShowCompleteDefinePromo(Boolean isShowCompleteDefinePromo) {
		this.isShowCompleteDefinePromo = isShowCompleteDefinePromo;
	}

	public Integer getCheckOpenFullNode() {
		return checkOpenFullNode;
	}

	public void setCheckOpenFullNode(Integer checkOpenFullNode) {
		this.checkOpenFullNode = checkOpenFullNode;
	}

	public List<Long> getLstShopIdNPP() {
		return lstShopIdNPP;
	}

	public void setLstShopIdNPP(List<Long> lstShopIdNPP) {
		this.lstShopIdNPP = lstShopIdNPP;
	}

	public Integer getOntop() {
		return ontop;
	}

	public void setOntop(Integer ontop) {
		this.ontop = ontop;
	}

	public String getLstBrandId() {
		return lstBrandId;
	}

	public void setLstBrandId(String lstBrandId) {
		this.lstBrandId = lstBrandId;
	}

	public String getLstCategoryId() {
		return lstCategoryId;
	}

	public void setLstCategoryId(String lstCategoryId) {
		this.lstCategoryId = lstCategoryId;
	}

	public String getLstSubCategoryId() {
		return lstSubCategoryId;
	}

	public void setLstSubCategoryId(String lstSubCategoryId) {
		this.lstSubCategoryId = lstSubCategoryId;
	}

	public PromotionNewcusConfigVO getPromotionNewcusConfig() {
		return promotionNewcusConfig;
	}

	public void setPromotionNewcusConfig(PromotionNewcusConfigVO promotionNewcusConfig) {
		this.promotionNewcusConfig = promotionNewcusConfig;
	}

	public Boolean getHaveRegulatedToCustlag() {
		return haveRegulatedToStaffFlag;
	}

	public void setHaveRegulatedToStaffFlag(Boolean haveRegulatedToStaffFlag) {
		this.haveRegulatedToStaffFlag = haveRegulatedToStaffFlag;
	}

	public Boolean getHaveRegulatedToCustFlag() {
		return haveRegulatedToCustFlag;
	}

	public void setHaveRegulatedToCustFlag(Boolean haveRegulatedToCustFlag) {
		this.haveRegulatedToCustFlag = haveRegulatedToCustFlag;
	}

	public String getNoticeCode() {
		return noticeCode;
	}

	public void setNoticeCode(String noticeCode) {
		this.noticeCode = noticeCode;
	}

	public String getDescriptionProduct() {
		return descriptionProduct;
	}

	public void setDescriptionProduct(String descriptionProduct) {
		this.descriptionProduct = descriptionProduct;
	}

	public Integer getDiscountType() {
		return discountType;
	}

	public void setDiscountType(Integer discountType) {
		this.discountType = discountType;
	}

	public Integer getRewardType() {
		return rewardType;
	}

	public void setRewardType(Integer rewardType) {
		this.rewardType = rewardType;
	}

	public long getIndexMua() {
		return indexMua;
	}

	public void setIndexMua(long indexMua) {
		this.indexMua = indexMua;
	}

	public long getIndexKM() {
		return indexKM;
	}

	public void setIndexKM(long indexKM) {
		this.indexKM = indexKM;
	}

	public String getNumberNotify() {
		return numberNotify;
	}

	public void setNumberNotify(String numberNotify) {
		this.numberNotify = numberNotify;
	}

	public Integer getFutureDate() {
		return futureDate;
	}

	public void setFutureDate(Integer futureDate) {
		this.futureDate = futureDate;
	}

	public String getFromApplyDate() {
		return fromApplyDate;
	}

	public String getToApplyDate() {
		return toApplyDate;
	}

	public void setFromApplyDate(String fromApplyDate) {
		this.fromApplyDate = fromApplyDate;
	}

	public void setToApplyDate(String toApplyDate) {
		this.toApplyDate = toApplyDate;
	}

	public Boolean getIsDiscount() {
		return isDiscount;
	}

	public void setIsDiscount(Boolean isDiscount) {
		this.isDiscount = isDiscount;
	}

	public Boolean getIsReward() {
		return isReward;
	}

	public void setIsReward(Boolean isReward) {
		this.isReward = isReward;
	}

	public List<CatalogVO> getLstStatus() {
		return lstStatus;
	}

	public void setLstStatus(List<CatalogVO> lstStatus) {
		this.lstStatus = lstStatus;
	}

	public Boolean getCheckPer() {
		return checkPer;
	}

	public void setCheckPer(Boolean checkPer) {
		this.checkPer = checkPer;
	}
	public Boolean getFlagExpire() {
		return flagExpire;
	}
	public void setFlagExpire(Boolean flagExpire) {
		this.flagExpire = flagExpire;
	}
	public List<ExMapping> getListSubLevelGroupZV192021() {
		return listSubLevelGroupZV192021;
	}
	public void setListSubLevelGroupZV192021(List<ExMapping> listSubLevelGroupZV192021) {
		this.listSubLevelGroupZV192021 = listSubLevelGroupZV192021;
	}
	
	public List<ExMapping> getListSubLevelConstraintZV07ZV12() {
		return listSubLevelConstraintZV07ZV12;
	}
	public void setListSubLevelConstraintZV07ZV12(List<ExMapping> listSubLevelConstraintZV07ZV12) {
		this.listSubLevelConstraintZV07ZV12 = listSubLevelConstraintZV07ZV12;
	}
	public Integer getExcelType() {
		return excelType;
	}
	public void setExcelType(Integer excelType) {
		this.excelType = excelType;
	}
	
	
}
