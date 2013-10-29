package com.changlianxi.util;

/**
 * �û���ϸ���Ϲ�����
 * 
 * @author teeker_bin
 * 
 */
public class UserInfoUtils {
	/* ������� */
	public static String infoTitleKey[] = { "������Ϣ", "��ϵ��ʽ", "�罻�˺�", "���ڵ�/ͨѶ��ַ",
			"��������", "��������", "��������" };
	/* �������ݶ԰���������key */
	public static String basicStr[] = { "D_NAME", "D_JOBTITLE", "D_EMPLOYER" };
	/* ��������key����Ӧ���������� */
	public static String basicChineseStr[] = { "����", "ͷ��", "������λ" };
	/* ��ϵ��ʽ���ݶ԰���������key */
	public static String contactStr[] = { "D_MOBILE", "D_WORK_PHONE",
			"D_HOME_PHONE", "D_HOME_FAX", "D_WORK_EMAIL", "D_WORK_FAX" };
	/* ��ϵ��ʽ����key����Ӧ���������� */
	public static String contacChinesetStr[] = { "�ֻ�", "�����绰", "��ͥ�绰", "��ͥ����",
			"����E-Mail", "��������" };
	/* �˺����ݶ԰���������key */
	public static String socialStr[] = { "D_QQ", "D_QQ_WEIBO", "D_SINA_WEIBO",
			"D_WEIXIN", "D_RENREN" };
	/* �˺�����key����Ӧ���������� */
	public static String socialChineseStr[] = { "QQ", "��Ѷ΢��", "����΢��", "΢��",
			"������" };
	/* ��ַ���ݶ԰���������key */
	public static String addressStr[] = { "D_BIRTH_PLACE", "D_CURRENT_ADDRESS",
			"D_HOME_ADDRESS", "D_WORK_ADDRESS" };
	/* ��ַ����key����Ӧ���������� */
	public static String addressChineseStr[] = { "����", "��ǰ��ס��", "��ͥסַ", "������ַ" };
	/* �������ݶ԰���������key */
	public static String otherStr[] = { "D_GENDAR", "D_BIRTHDAY" };
	/* ��������key����Ӧ���������� */
	public static String otherChineseStr[] = { "�Ԅe", "����" };
	/* ������������������key */
	public static String eduStr[] = { "D_COLLEGE", "D_GRADE_SCHOOL",
			"D_JUNIOR_COLLEGE", "D_JUNIOR_SCHOOL", "D_KINDER_GARTEN",
			"D_MASTER_COLLEGE", "D_PHD_COLLEGE", "D_SENIOR_SCHOOL" };
	/* ����key����Ӧ���������� */
	public static String eduChinesStr[] = { "��ѧ", "Сѧ", "��ר", "����", "�׶�԰",
			"˶ʿ", "��ʿ", "����" };
	/* ��������������������key */
	public static String workStr[] = { "D_JOB" };
	/* ����key����Ӧ���������� */
	public static String workChineseStr[] = { "����" };
	/* ��������type */
	public static String infoKey[] = { "D_NAME", "D_JOBTITLE", "D_EMPLOYER",
			"D_MOBILE", "D_WORK_PHONE", "D_HOME_PHONE", "D_HOME_FAX",
			"D_WORK_EMAIL", "D_WORK_FAX,", "D_QQ", "D_QQ_WEIBO",
			"D_SINA_WEIBO", "D_WEIXIN", "D_RENREN", "D_BIRTH_PLACE",
			"D_CURRENT_ADDRESS", "D_HOME_ADDRESS", "D_WORK_ADDRESS",
			"D_GENDAR", "D_BIRTHDAY", "D_COLLEGE", "D_GRADE_SCHOOL",
			"D_JUNIOR_COLLEGE", "D_JUNIOR_SCHOOL", "D_KINDER_GARTEN",
			"D_MASTER_COLLEGE", "D_PHD_COLLEGE", "D_SENIOR_SCHOOL", "D_JOB" };
	public static String infoKeyChinese[] = { "����", "ͷ��", "������λ", "�ֻ�", "�����绰",
			"��ͥ�绰", "��ͥ����", "����E-Mail", "��������", "QQ", "��Ѷ΢��", "����΢��", "΢��",
			"������", "����", "��ǰ��ס��", "��ͥסַ", "������ַ", "�Ԅe", "����", "��ѧ", "Сѧ", "��ר",
			"����", "�׶�԰", "˶ʿ", "��ʿ", "����", "����" };

	/**
	 * ��Ӣ��keyװ��Ϊ����key
	 * 
	 * @param key
	 *            Ҫת����key
	 */
	public static String convertToChines(String key) {
		for (int i = 0; i < infoKey.length; i++) {
			if (infoKey[i].equals(key)) {
				return infoKeyChinese[i];
			}
		}
		return key;

	}

	/**
	 * ������keyװ��ΪӢ��key
	 * 
	 * @param key
	 *            Ҫת����key
	 */
	public static String convertToEnglish(String key) {
		for (int i = 0; i < infoKeyChinese.length; i++) {
			if (infoKeyChinese[i].equals(key)) {
				return infoKey[i];
			}
		}
		return key;

	}
}
