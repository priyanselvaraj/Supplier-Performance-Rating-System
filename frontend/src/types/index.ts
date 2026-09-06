// SPRS TypeScript Type Definitions matching Backend DTOs

export type Role = 'ROLE_ADMIN' | 'ROLE_MANAGER';

export type SupplierStatus = 'ACTIVE' | 'INACTIVE' | 'PENDING_REVIEW';

export type SupplierRating = 'EXCELLENT' | 'VERY_GOOD' | 'GOOD' | 'AVERAGE' | 'POOR';

export type PerformanceStatus = 'HIGH_PERFORMING' | 'SATISFACTORY' | 'NEEDS_IMPROVEMENT' | 'LOW_PERFORMING';

export type PerformanceTrend = 'IMPROVING' | 'STABLE' | 'DECLINING' | 'INSUFFICIENT_DATA';

export type EvaluationStatus = 'DRAFT' | 'SUBMITTED' | 'COMPLETED' | 'CANCELLED';

export interface User {
  id: number;
  username: string;
  email: string;
  fullName: string;
  phoneNumber?: string;
  roles: string[];
  active: boolean;
}

export interface SupplierCategory {
  id: number;
  name: string;
  description?: string;
  active: boolean;
  createdAt?: string;
  updatedAt?: string;
}

export interface Supplier {
  id: number;
  supplierCode: string;
  name: string;
  contactPerson?: string;
  email: string;
  phone?: string;
  address?: string;
  city?: string;
  state?: string;
  country?: string;
  website?: string;
  status: SupplierStatus;
  overallRating: number;
  ratingCategory?: string;
  totalEvaluations: number;
  category?: SupplierCategory;
  createdAt?: string;
  updatedAt?: string;
}

export interface EvaluationCriteria {
  id: number;
  name: string;
  code: string;
  description?: string;
  weight: number;
  maxScore: number;
  active: boolean;
  createdAt?: string;
}

export interface EvaluationScore {
  id?: number;
  criteriaId: number;
  criteriaName?: string;
  weight?: number;
  scoreObtained: number;
  maxScore?: number;
  weightedScore?: number;
  remarks?: string;
}

export interface SupplierEvaluation {
  id: number;
  evaluationCode: string;
  supplierId: number;
  supplierName?: string;
  supplierCode?: string;
  evaluatorId?: number;
  evaluatorName?: string;
  evaluationDate: string;
  evaluationPeriod?: string;
  status: EvaluationStatus;
  totalWeightedScore: number;
  generalComments?: string;
  strengths?: string;
  areasForImprovement?: string;
  recommendation?: string;
  scores: EvaluationScore[];
  createdAt?: string;
  updatedAt?: string;
}

export interface SupplierPerformanceRating {
  id: number;
  supplierId: number;
  supplierName?: string;
  supplierCode?: string;
  score: number;
  rating: SupplierRating;
  ratingDisplayName: string;
  performanceStatus: PerformanceStatus;
  performanceStatusDisplayName: string;
  ratingDate: string;
  evaluationId?: number;
  evaluationCode?: string;
  createdAt?: string;
}

export interface SupplierPerformanceSummary {
  supplierId: number;
  supplierCode: string;
  supplierName: string;
  categoryName?: string;
  active: boolean;
  latestScore: number;
  latestRating: SupplierRating;
  latestRatingDisplayName: string;
  latestPerformanceStatus: PerformanceStatus;
  performanceStatusDisplayName: string;
  previousScore?: number;
  scoreDifference?: number;
  performanceTrend: PerformanceTrend;
  performanceTrendDisplayName: string;
  latestRatingDate: string;
  totalEvaluations: number;
}

export interface DashboardSummary {
  totalSuppliers: number;
  activeSuppliers: number;
  inactiveSuppliers: number;
  totalCategories: number;
  totalEvaluations: number;
  completedEvaluations: number;
  draftEvaluations: number;
  submittedEvaluations: number;
  averagePerformanceScore: number;
  highPerformingSuppliersCount: number;
  needsImprovementSuppliersCount: number;
  ratingDistribution: { rating: SupplierRating; ratingDisplayName: string; count: number; percentage: number }[];
  performanceStatusDistribution: { status: PerformanceStatus; statusDisplayName: string; count: number; percentage: number }[];
}

export interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
  timestamp?: string;
}

export interface PaginatedResponse<T> {
  content: T[];
  pageNumber: number;
  pageSize: number;
  totalElements: number;
  totalPages: number;
  last: boolean;
}

export type RiskLevel = 'LOW' | 'MEDIUM' | 'HIGH' | 'CRITICAL';
export type AlertSeverity = 'INFO' | 'WARNING' | 'HIGH' | 'CRITICAL';
export type AlertType = 'PERFORMANCE_DECLINE' | 'LOW_PERFORMANCE' | 'REPEATED_POOR_PERFORMANCE' | 'HIGH_RISK_SUPPLIER' | 'CRITERIA_DEFICIENCY';
export type AlertStatus = 'ACTIVE' | 'ACKNOWLEDGED' | 'RESOLVED';
export type RecommendationPriority = 'LOW' | 'MEDIUM' | 'HIGH' | 'CRITICAL';
export type ConfidenceLevel = 'LOW' | 'MEDIUM' | 'HIGH';

export interface SupplierPrediction {
  supplierId: number;
  supplierName: string;
  supplierCode: string;
  currentScore: number;
  predictedScore?: number;
  confidence: ConfidenceLevel;
  trend: PerformanceTrend;
  velocityRate: number;
  historicalEvaluationsCount: number;
  historicalScores: number[];
  predictionMethod: string;
  explanation: string;
  sufficientData: boolean;
  statusMessage: string;
}

export interface SupplierRisk {
  supplierId: number;
  supplierName: string;
  supplierCode: string;
  currentScore: number;
  ratingCategory?: string;
  trend: PerformanceTrend;
  riskScore: number;
  riskLevel: RiskLevel;
  riskFactors: string[];
  explanation: string;
  activeAlertsCount: number;
}

export interface AiRecommendation {
  id: string;
  supplierId: number;
  criterionName: string;
  currentScore: number;
  maxScore: number;
  percentage: number;
  priority: RecommendationPriority;
  title: string;
  recommendation: string;
  actionableSteps: string;
}

export interface AiAlert {
  id: string;
  supplierId: number;
  supplierName: string;
  supplierCode: string;
  alertType: AlertType;
  severity: AlertSeverity;
  title: string;
  message: string;
  triggeredDate: string;
  status: AlertStatus;
}

export interface AiDashboardSummary {
  totalSuppliersAnalyzed: number;
  lowRiskCount: number;
  mediumRiskCount: number;
  highRiskCount: number;
  criticalRiskCount: number;
  decliningSuppliersCount: number;
  activeAlertsCount: number;
  averageSystemRiskScore: number;
  riskDistribution: Record<RiskLevel, number>;
  criticalAlerts: AiAlert[];
  topRiskSuppliers: SupplierRisk[];
  decliningPredictions: SupplierPrediction[];
  topRecommendations: AiRecommendation[];
}

export type NotificationType = 'SYSTEM' | 'SUPPLIER' | 'EVALUATION' | 'RATING' | 'ALERT' | 'AI_INSIGHT' | 'IMPROVEMENT_ACTION';
export type NotificationPriority = 'LOW' | 'MEDIUM' | 'HIGH' | 'CRITICAL';
export type ImprovementActionStatus = 'OPEN' | 'IN_PROGRESS' | 'COMPLETED' | 'CANCELLED';
export type ImprovementActionPriority = 'LOW' | 'MEDIUM' | 'HIGH' | 'CRITICAL';

export interface NotificationItem {
  id: number;
  userId: number;
  username: string;
  title: string;
  message: string;
  notificationType: NotificationType;
  priority: NotificationPriority;
  relatedResourceType?: string;
  relatedResourceId?: number;
  read: boolean;
  createdAt: string;
  readAt?: string;
}

export interface UserNotificationPreference {
  id?: number;
  notificationType: NotificationType;
  displayName: string;
  inAppEnabled: boolean;
  emailEnabled: boolean;
}

export interface SupplierImprovementAction {
  id: number;
  supplierId: number;
  supplierName: string;
  supplierCode: string;
  title: string;
  description: string;
  priority: ImprovementActionPriority;
  status: ImprovementActionStatus;
  assignedUserId?: number;
  assignedUserName?: string;
  createdByUserId?: number;
  createdByUserName?: string;
  dueDate?: string;
  completedAt?: string;
  resolutionNotes?: string;
  createdAt: string;
  updatedAt: string;
}

export interface MonitoringSummary {
  totalSuppliers: number;
  activeSuppliers: number;
  highRiskSuppliersCount: number;
  criticalAlertsCount: number;
  openImprovementActionsCount: number;
  inProgressImprovementActionsCount: number;
  completedImprovementActionsCount: number;
  recentEvaluationsCount: number;
  unreadNotificationsCount: number;
  systemHealthStatus: 'HEALTHY' | 'WARNING' | 'ATTENTION_REQUIRED';
  averageEvaluationScore: number;
  criticalAlerts: AiAlert[];
  topRiskWatchlist: SupplierRisk[];
  urgentActions: SupplierImprovementAction[];
  timestamp: string;
}

