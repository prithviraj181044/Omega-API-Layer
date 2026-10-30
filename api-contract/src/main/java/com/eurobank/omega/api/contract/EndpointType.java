package com.eurobank.omega.api.contract;

/** Identifies the HTTP endpoint family; TCP families are parked. */
public enum EndpointType {
  /** Rest value. */
  REST,

  /** Altamira rest value. */
  ALTAMIRA_REST,

  /** As400 rest value. */
  AS400_REST;
}
