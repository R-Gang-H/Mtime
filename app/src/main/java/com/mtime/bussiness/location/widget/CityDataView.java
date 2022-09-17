/**
 * 
 */
package com.mtime.bussiness.location.widget;

import com.mtime.frame.BaseActivity;
import com.mtime.bussiness.location.bean.ChangeCitySortBean;
import com.mtime.bussiness.location.bean.ChinaProvincesBean;
import com.mtime.bussiness.location.bean.CityBean;
import com.mtime.bussiness.location.bean.ProvinceBean;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wangjin
 * 
 */
public class CityDataView{
    /**
     * @param activity
     */
    public CityDataView(BaseActivity activity) {
    }

    public ArrayList<CityBean> getCityBeans(final ChinaProvincesBean chinaProvincesBean) {

        final ArrayList<CityBean> aCityBeans = new ArrayList<CityBean>();
        for (int i = 0; i < chinaProvincesBean.getProvinces().size(); i++) {
            aCityBeans.add(provincesTocityBean(chinaProvincesBean.getProvinces().get(i)));

        }
        return aCityBeans;

    }

    public ArrayList<ChangeCitySortBean> changeCitySortBean(final ChinaProvincesBean chinaProvincesBean,
            final List<ProvinceBean> arrayList) {
        final ArrayList<CityBean> aCityBeans = getCityBeans(chinaProvincesBean);
        ArrayList<ChangeCitySortBean> changeCitySortBeans = new ArrayList<>();
        final ChangeCitySortBean aBean = new ChangeCitySortBean();
        final ChangeCitySortBean bBean = new ChangeCitySortBean();
        final ChangeCitySortBean cBean = new ChangeCitySortBean();
        final ChangeCitySortBean dBean = new ChangeCitySortBean();
        final ChangeCitySortBean eBean = new ChangeCitySortBean();
        final ChangeCitySortBean fBean = new ChangeCitySortBean();
        final ChangeCitySortBean gBean = new ChangeCitySortBean();
        final ChangeCitySortBean hBean = new ChangeCitySortBean();
        final ChangeCitySortBean iBean = new ChangeCitySortBean();
        final ChangeCitySortBean jBean = new ChangeCitySortBean();
        final ChangeCitySortBean kBean = new ChangeCitySortBean();
        final ChangeCitySortBean lBean = new ChangeCitySortBean();
        final ChangeCitySortBean mBean = new ChangeCitySortBean();
        final ChangeCitySortBean nBean = new ChangeCitySortBean();
        final ChangeCitySortBean oBean = new ChangeCitySortBean();
        final ChangeCitySortBean pBean = new ChangeCitySortBean();
        final ChangeCitySortBean qBean = new ChangeCitySortBean();
        final ChangeCitySortBean rBean = new ChangeCitySortBean();
        final ChangeCitySortBean sBean = new ChangeCitySortBean();
        final ChangeCitySortBean tBean = new ChangeCitySortBean();
        final ChangeCitySortBean uBean = new ChangeCitySortBean();
        final ChangeCitySortBean vBean = new ChangeCitySortBean();
        final ChangeCitySortBean wBean = new ChangeCitySortBean();
        final ChangeCitySortBean xBean = new ChangeCitySortBean();
        final ChangeCitySortBean yBean = new ChangeCitySortBean();
        final ChangeCitySortBean zBean = new ChangeCitySortBean();

        for (int i = 0; i < aCityBeans.size(); i++) {
            if (aCityBeans.get(i).getPinyinFull() != null) {
                if (aCityBeans.get(i).getPinyinFull().toUpperCase().startsWith("A")) {
                    aBean.getCityBeans().add(aCityBeans.get(i));
                }
                else if (aCityBeans.get(i).getPinyinFull().toUpperCase().startsWith("B")) {
                    bBean.getCityBeans().add(aCityBeans.get(i));
                }
                else if (aCityBeans.get(i).getPinyinFull().toUpperCase().startsWith("C")) {
                    cBean.getCityBeans().add(aCityBeans.get(i));
                }
                else if (aCityBeans.get(i).getPinyinFull().toUpperCase().startsWith("D")) {
                    dBean.getCityBeans().add(aCityBeans.get(i));
                }
                else if (aCityBeans.get(i).getPinyinFull().toUpperCase().startsWith("E")) {
                    eBean.getCityBeans().add(aCityBeans.get(i));
                }
                else if (aCityBeans.get(i).getPinyinFull().toUpperCase().startsWith("F")) {
                    fBean.getCityBeans().add(aCityBeans.get(i));
                }
                else if (aCityBeans.get(i).getPinyinFull().toUpperCase().startsWith("G")) {
                    gBean.getCityBeans().add(aCityBeans.get(i));
                }
                else if (aCityBeans.get(i).getPinyinFull().toUpperCase().startsWith("H")) {
                    hBean.getCityBeans().add(aCityBeans.get(i));
                }
                else if (aCityBeans.get(i).getPinyinFull().toUpperCase().startsWith("I")) {
                    iBean.getCityBeans().add(aCityBeans.get(i));
                }
                else if (aCityBeans.get(i).getPinyinFull().toUpperCase().startsWith("J")) {
                    jBean.getCityBeans().add(aCityBeans.get(i));
                }
                else if (aCityBeans.get(i).getPinyinFull().toUpperCase().startsWith("K")) {
                    kBean.getCityBeans().add(aCityBeans.get(i));
                }
                else if (aCityBeans.get(i).getPinyinFull().toUpperCase().startsWith("L")) {
                    lBean.getCityBeans().add(aCityBeans.get(i));
                }
                else if (aCityBeans.get(i).getPinyinFull().toUpperCase().startsWith("M")) {
                    mBean.getCityBeans().add(aCityBeans.get(i));
                }
                else if (aCityBeans.get(i).getPinyinFull().toUpperCase().startsWith("N")) {
                    nBean.getCityBeans().add(aCityBeans.get(i));
                }
                else if (aCityBeans.get(i).getPinyinFull().toUpperCase().startsWith("O")) {
                    oBean.getCityBeans().add(aCityBeans.get(i));
                }
                else if (aCityBeans.get(i).getPinyinFull().toUpperCase().startsWith("P")) {
                    pBean.getCityBeans().add(aCityBeans.get(i));
                }
                else if (aCityBeans.get(i).getPinyinFull().toUpperCase().startsWith("Q")) {
                    qBean.getCityBeans().add(aCityBeans.get(i));
                }
                else if (aCityBeans.get(i).getPinyinFull().toUpperCase().startsWith("R")) {
                    rBean.getCityBeans().add(aCityBeans.get(i));
                }
                else if (aCityBeans.get(i).getPinyinFull().toUpperCase().startsWith("S")) {
                    sBean.getCityBeans().add(aCityBeans.get(i));
                }
                else if (aCityBeans.get(i).getPinyinFull().toUpperCase().startsWith("T")) {
                    tBean.getCityBeans().add(aCityBeans.get(i));
                }
                else if (aCityBeans.get(i).getPinyinFull().toUpperCase().startsWith("U")) {
                    uBean.getCityBeans().add(aCityBeans.get(i));
                }
                else if (aCityBeans.get(i).getPinyinFull().toUpperCase().startsWith("V")) {
                    vBean.getCityBeans().add(aCityBeans.get(i));
                }
                else if (aCityBeans.get(i).getPinyinFull().toUpperCase().startsWith("W")) {
                    wBean.getCityBeans().add(aCityBeans.get(i));
                }
                else if (aCityBeans.get(i).getPinyinFull().toUpperCase().startsWith("X")) {
                    xBean.getCityBeans().add(aCityBeans.get(i));
                }
                else if (aCityBeans.get(i).getPinyinFull().toUpperCase().startsWith("Y")) {
                    yBean.getCityBeans().add(aCityBeans.get(i));
                }
                else if (aCityBeans.get(i).getPinyinFull().toUpperCase().startsWith("Z")) {
                    zBean.getCityBeans().add(aCityBeans.get(i));
                }
            }

        }
        if ((arrayList == null) || (arrayList.size() == 0)) {

        }
        else {
            changeCitySortBeans.add(hotCitySortBean(arrayList));
        }

        changeCitySortBeans.add(aBean);
        changeCitySortBeans.add(bBean);
        changeCitySortBeans.add(cBean);
        changeCitySortBeans.add(dBean);
        changeCitySortBeans.add(eBean);
        changeCitySortBeans.add(fBean);
        changeCitySortBeans.add(gBean);
        changeCitySortBeans.add(hBean);
        changeCitySortBeans.add(iBean);
        changeCitySortBeans.add(jBean);
        changeCitySortBeans.add(kBean);
        changeCitySortBeans.add(lBean);
        changeCitySortBeans.add(mBean);
        changeCitySortBeans.add(nBean);
        changeCitySortBeans.add(oBean);
        changeCitySortBeans.add(pBean);
        changeCitySortBeans.add(qBean);
        changeCitySortBeans.add(rBean);
        changeCitySortBeans.add(sBean);
        changeCitySortBeans.add(tBean);
        changeCitySortBeans.add(uBean);
        changeCitySortBeans.add(vBean);
        changeCitySortBeans.add(wBean);
        changeCitySortBeans.add(xBean);
        changeCitySortBeans.add(yBean);
        changeCitySortBeans.add(zBean);
        for (int j = changeCitySortBeans.size() - 1; j >= 0; j--) {
            if (changeCitySortBeans.get(j).getCityBeans().size() <= 0) {
                changeCitySortBeans.remove(j);
            }

        }
        return changeCitySortBeans;

    }

    public CityBean provincesTocityBean(final ProvinceBean provinceBean) {
        final CityBean cityBeans = new CityBean();
        cityBeans.setId(provinceBean.getId());
        cityBeans.setN(provinceBean.getName());
        if (provinceBean.getPinyinShort() != null) {
            cityBeans.setPinyinShort(provinceBean.getPinyinShort());
        }
        if (provinceBean.getPinyinFull() != null) {
            cityBeans.setPinyinFull(provinceBean.getPinyinFull());
        }

        return cityBeans;

    }

    public ChangeCitySortBean hotCitySortBean(final List<ProvinceBean> arrayList) {
        final ChangeCitySortBean hotchangeBean = new ChangeCitySortBean();
        for (int i = 0; i < arrayList.size(); i++) {
            final CityBean cityBean = new CityBean();
            cityBean.setId(arrayList.get(i).getId());
            cityBean.setN(arrayList.get(i).getName());
            hotchangeBean.getCityBeans().add(cityBean);

        }

        return hotchangeBean;

    }
}
