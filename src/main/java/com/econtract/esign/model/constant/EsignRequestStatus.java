/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.econtract.esign.model.constant;

/**
 *
 * @author TS
 */
public class EsignRequestStatus {

    public static int NEW = 1;
    public static int INITIATED = 2;
    public static int SINGED_BY_CUSTOMER = 3;
    public static int SINGED_BY_BUSINESS_SIGNATOR = 4;
    public static int PRINTED = 5;
    public static int DISPATCHED = 6;
    public static int COMPLETED = 7;
    public static int CANCELLED = 8;
    public static int AGREEMENT_DOWNLOADED = 9;//not in used
    public static int AGREEMENT_UPLOADED = 10;
    public static int WAITING_FOR_INITIATE = 11;
    public static int INITIATIATION_FAILED = 12;
    public static int AWAITING_STAMP_PAPER = 13;

    public static boolean isValid(int o) {
        if (NEW == o || INITIATED == o || SINGED_BY_CUSTOMER == o || SINGED_BY_BUSINESS_SIGNATOR == o
                || PRINTED == o || DISPATCHED == o || COMPLETED == o || CANCELLED == o) {
            return true;
        }
        return false;
    }

    public static boolean isBeforeInitiate(int o) {
        if (NEW == 0 || WAITING_FOR_INITIATE == 0 || WAITING_FOR_INITIATE == 0 || AWAITING_STAMP_PAPER == o) {
            return true;
        }
        return false;
    }

    public static String getStatusName(int o) {
        String name = "";

        if (o == NEW) {
            name = "NEW";
        } else if (o == INITIATED) {
            name = "INITIATED";
        } else if (o == SINGED_BY_CUSTOMER) {
            name = "SINGED_BY_CUSTOMER";
        } else if (o == SINGED_BY_BUSINESS_SIGNATOR) {
            name = "SINGED_BY_BUSINESS_SIGNATOR";
        } else if (o == PRINTED) {
            name = "PRINTED";
        } else if (o == DISPATCHED) {
            name = "DISPATCHED";
        } else if (o == COMPLETED) {
            name = "COMPLETED";
        } else if (o == CANCELLED) {
            name = "CANCELLED";
        } else if (o == WAITING_FOR_INITIATE) {
            name = "WAITING_FOR_INITIATE";
        } else if (o == INITIATIATION_FAILED) {
            name = "INITIATIATION_FAILED";
        } else if (o == AWAITING_STAMP_PAPER) {
            name = "AWAITING_STAMP_PAPER";
        }
        return name;
    }
}
