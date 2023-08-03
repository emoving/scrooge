import React, { useEffect } from "react";
import styles from "./MyPageExpBar.module.css";

const MyPageExpBar = ({totalExp, currentExp}) => {
  const calculatePercentage = (current,total) => {
    return (current / total) * 100;
  };

  const expBarStyle = {
    width: `${calculatePercentage(currentExp, totalExp)}%`,    
  };

  useEffect(()=>{
  }, [totalExp, currentExp]);


  return (
    <div className={["exp-container"]}>
      <div className={styles.expBar}>
        {/* 전체 경험치 바 */}
        <div className={styles.totalExpBar}>
          <div className={styles.progressBar}></div>
        </div>

        {/* 현재 경험치 바 */}
        <div className={styles.currentExpBar} style={expBarStyle}></div>
        {/* <div className={styles.currentExpBar}>
          <div className={styles.progressBar}></div>
        </div> */}
      </div>
      <img src={`${process.env.PUBLIC_URL}/images/exp-icon.png`} alt="경험치 아이콘"/>
    </div>

  );
};

export default MyPageExpBar;