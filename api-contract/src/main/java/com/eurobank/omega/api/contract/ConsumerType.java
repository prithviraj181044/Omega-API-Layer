package com.eurobank.omega.api.contract;

/** Classifies an authenticated API consumer. */
public enum ConsumerType {
  /** Channel value. */
  CHANNEL,

  /** Application value. */
  APPLICATION,

  /** Service value. */
  SERVICE,

  /** Mcp tool value. */
  MCP_TOOL,

  /** Agent value. */
  AGENT,

  /** System integration value. */
  SYSTEM_INTEGRATION;
}
